package vn.ehealth.hl7.fhir.factory;

import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.RestfulServerUtils;
import ca.uhn.fhir.rest.server.exceptions.*;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
//import org.xhtmlrenderer.pdf.ITextRenderer;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ServerInterceptor extends InterceptorAdapter {
	// https://en.wikipedia.org/wiki/List_of_HTTP_header_fields#Field_names
	private Logger log = null; // LoggerFactory.getLogger(ServerInterceptor.class);
	@SuppressWarnings("unused")
	private String myErrorMessageFormat = "ERROR - ${operationType} - ${idOrResourceName}";

	FhirContext ctx = FhirContext.forR4();

	public ServerInterceptor(Logger ourLog) {
		super();
		this.log = ourLog;

	}

	@Override
	public boolean handleException(RequestDetails theRequestDetails, BaseServerResponseException theException,
			HttpServletRequest theServletRequest, HttpServletResponse theServletResponse)
			throws ServletException, IOException {

		// Return false when overriding hapi behaviour.
		log.info("Exception = " + theException.getClass().getCanonicalName());
		if (theException instanceof InvalidRequestException) {
			if (theException.getOperationOutcome() != null
					&& (theException.getOperationOutcome() instanceof OperationOutcome
							|| theException.getOperationOutcome() instanceof IBaseOperationOutcome)) {
				FhirContext ctx = FhirContext.forR4();

				OperationOutcome outcome = (OperationOutcome) theException.getOperationOutcome();
				log.info("Exception intercept. Diagnostic Response = " + outcome.getIssueFirstRep().getDiagnostics()
						+ " " + outcome.getIssueFirstRep().getCode().getDisplay());
				if (outcome.getIssueFirstRep().getCode().equals(OperationOutcome.IssueType.PROCESSING)) {
					if (outcome.getIssueFirstRep().getDiagnostics()
							.contains("The FHIR endpoint on this server does not know how to handle")) {

						if (outcome.getIssueFirstRep().getDiagnostics().contains("handle GET")) {
							if (outcome.getIssueFirstRep().getDiagnostics().contains("[coffee]"))
								theServletResponse.setStatus(418);
							else
								theServletResponse.setStatus(404);
						} else {
							theServletResponse.setStatus(501);
						}
						if (outcome.getIssueFirstRep().getDiagnostics() != null) {
							log.debug("Diagnostic Response = " + outcome.getIssueFirstRep().getDiagnostics());

						}

						// Provide a response yourself
						theServletResponse.setContentType("application/json+fhir;charset=UTF-8");

						theServletResponse.getWriter()
								.append(ctx.newJsonParser().encodeResourceToString(theException.getOperationOutcome()));
						theServletResponse.getWriter().close();
						return false;
					}
					if (outcome.getIssueFirstRep().getDiagnostics().contains("Unsupported media type:")) {
						if (theRequestDetails.getResourceName() == null
								|| (!theRequestDetails.getResourceName().equals("Binary"))) {
							theServletResponse.setStatus(406);
							theServletResponse.setContentType("application/json+fhir;charset=UTF-8");
							theServletResponse.getWriter().append(
									ctx.newJsonParser().encodeResourceToString(theException.getOperationOutcome()));
							theServletResponse.getWriter().close();
							return false;
						}

					}
				}
			}

		}
		if (theException instanceof InternalErrorException) {
			if (theException.getOperationOutcome() != null
					&& theException.getOperationOutcome() instanceof OperationOutcome) {
				FhirContext ctx = FhirContext.forR4();

				OperationOutcome outcome = (OperationOutcome) theException.getOperationOutcome();
				log.error("InternalErrorException: Diagnostics = " + outcome.getIssueFirstRep().getDiagnostics() + " "
						+ outcome.getIssueFirstRep().getCode().getDisplay());
				if (outcome.getIssueFirstRep().getCode().equals(OperationOutcome.IssueType.PROCESSING)) {

					theServletResponse.setStatus(400);
					if (outcome.getIssueFirstRep().getDiagnostics() != null) {
						log.debug("Diagnostic Response = " + outcome.getIssueFirstRep().getDiagnostics());

					}

					// Provide a response ourself
					theServletResponse.setContentType("application/json+fhir;charset=UTF-8");

					theServletResponse.getWriter()
							.append(ctx.newJsonParser().encodeResourceToString(theException.getOperationOutcome()));
					theServletResponse.getWriter().close();
					return false;
				}
			}
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean incomingRequestPostProcessed(RequestDetails theRequestDetails, HttpServletRequest theRequest,
			HttpServletResponse theResponse) throws AuthenticationException {
		log.trace("incomingRequestPostProcessed " + theRequest.getMethod());
		Enumeration<String> headers = theRequest.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			log.debug("Header  = " + header + "=" + theRequest.getHeader(header));
		}
		// Perform any string substitutions from the message format
		StrLookup<?> lookup = new MyLookup(theRequest, theRequestDetails);
		StrSubstitutor subs = new StrSubstitutor(lookup, "${", "}", '\\');

		// Actually log the line
		String myMessageFormat = "httpVerb[${requestVerb}] Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] RequestId[${requestHeader.x-request-id}] ForwardedFor[${requestHeader.x-forwarded-for}] ForwardedHost[${requestHeader.x-forwarded-host}] CorrelationId[] ProcessingTime[]  ResponseCode[]";

		String line = subs.replace(myMessageFormat);
		log.info(line);

		return true;
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	@Override
	public boolean incomingRequestPreProcessed(HttpServletRequest request, HttpServletResponse theResponse) {
		log.trace("incomingRequestPreProcessed " + request.getMethod());
		if (request.getMethod() != null && (!request.getRequestURI().contains("Binary"))) {

			if (request.getContentType() != null) {
				checkContentType(request.getContentType());
			}

			if (request.getHeader("Accept") != null && !request.getHeader("Accept").equals("*/*")) {
				checkContentType(request.getHeader("Accept"));
			}

			if (request.getQueryString() != null) {

				List<NameValuePair> params = null;
				try {
					params = URLEncodedUtils.parse(new URI("http://dummy?" + request.getQueryString()), "UTF-8");
				} catch (Exception ex) {
				}

				ListIterator paramlist = params.listIterator();
				while (paramlist.hasNext()) {
					NameValuePair param = (NameValuePair) paramlist.next();
					if (param.getName().equals("_format"))
						checkContentType(param.getValue());

				}
			}
		}
		return true;
	}

	public void checkContentType(String contentType) {
		try {
			MediaType media = MediaType.parseMediaType(contentType);
			// TODO improve the logic here

			if (!contentType.contains("xml") && !contentType.contains("json")
					&& !contentType.contains("x-www-form-urlencoded")) {
				log.debug("Unsupported media type: " + contentType);
				throw new InvalidRequestException("Unsupported media type: content " + contentType);
			} else if (media.getSubtype() != null && !media.getSubtype().contains("xml")
					&& !media.getSubtype().contains("fhir") && !media.getSubtype().contains("json")) {
				log.debug("Unsupported media type: " + contentType);
				throw new InvalidRequestException("Unsupported media type: sub " + contentType);
			}

		} catch (InvalidMediaTypeException e) {
			log.debug("Unsupported media type: " + contentType);
			if (!contentType.contains("xml") && !contentType.contains("json")
					&& !contentType.contains("x-www-form-urlencoded")) {
				log.debug("Unsupported media type: " + contentType);
				throw new InvalidRequestException("Unsupported media type: content " + contentType);
			}
		}
	}

	@Override
	public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject) {
		ServletRequestDetails details = (ServletRequestDetails) theRequestDetails;
		log.debug("outgoingResponse2 = ");
		return outgoingResponse(details, theResponseObject, details.getServletRequest(), details.getServletResponse());
	}

	@Override
	public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource resource,
			HttpServletRequest theServletRequest, HttpServletResponse response) throws AuthenticationException {

		String val = theRequestDetails.getHeader("x-request-id");

		if (val != null && !val.isEmpty()) {
			response.addHeader("X-Correlation-ID", val);
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void processingCompletedNormally(ServletRequestDetails theRequestDetails) {
		// Perform any string substitutions from the message format

		StrLookup<?> lookup = new MyLookup(theRequestDetails.getServletRequest(), theRequestDetails);
		StrSubstitutor subs = new StrSubstitutor(lookup, "${", "}", '\\');

		for (String header : theRequestDetails.getServletResponse().getHeaderNames()) {
			log.debug("Header  = " + header + "=" + theRequestDetails.getServletResponse().getHeader(header));
		}

		String myMessageFormat = "httpVerb[${requestVerb}] Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] UA[${requestHeader.user-agent}] Params[${requestParameters}] RequestId[${requestHeader.x-request-id}] ForwardedFor[${requestHeader.x-forwarded-for}] ForwardedHost[${requestHeader.x-forwarded-host}] CorrelationId[${requestHeader.x-request-id}] ProcessingTime[${processingTimeMillis}]";

		String line = subs.replace(myMessageFormat);
		log.info(line + " ResponseCode[" + theRequestDetails.getServletResponse().getStatus() + "]");
	}

	@SuppressWarnings("deprecation")
	private static final class MyLookup extends StrLookup<String> {
		private final Throwable myException;
		private final HttpServletRequest myRequest;
		private final RequestDetails myRequestDetails;

		private MyLookup(HttpServletRequest theRequest, RequestDetails theRequestDetails) {
			myRequest = theRequest;
			myRequestDetails = theRequestDetails;
			myException = null;
		}

		@SuppressWarnings("unused")
		public MyLookup(HttpServletRequest theServletRequest, BaseServerResponseException theException,
				RequestDetails theRequestDetails) {
			myException = theException;
			myRequestDetails = theRequestDetails;
			myRequest = theServletRequest;
		}

		@Override
		public String lookup(String theKey) {

			/*
			 * TODO: this method could be made more efficient through some sort of lookup
			 * map
			 */

			if ("operationType".equals(theKey)) {
				if (myRequestDetails.getRestOperationType() != null) {
					return myRequestDetails.getRestOperationType().getCode();
				}
				return "";
			} else if ("operationName".equals(theKey)) {
				if (myRequestDetails.getRestOperationType() != null) {
					switch (myRequestDetails.getRestOperationType()) {
					case EXTENDED_OPERATION_INSTANCE:
					case EXTENDED_OPERATION_SERVER:
					case EXTENDED_OPERATION_TYPE:
						return myRequestDetails.getOperation();
					default:
						return "";
					}
				}
				return "";
			} else if ("id".equals(theKey)) {
				if (myRequestDetails.getId() != null) {
					return myRequestDetails.getId().getValue();
				}
				return "";
			} else if ("servletPath".equals(theKey)) {
				return StringUtils.defaultString(myRequest.getServletPath());
			} else if ("idOrResourceName".equals(theKey)) {
				if (myRequestDetails.getId() != null) {
					return myRequestDetails.getId().getValue();
				}
				if (myRequestDetails.getResourceName() != null) {
					return myRequestDetails.getResourceName();
				}
				return "";
			} else if (theKey.equals("requestParameters")) {
				StringBuilder b = new StringBuilder();
				for (Map.Entry<String, String[]> next : myRequestDetails.getParameters().entrySet()) {
					for (String nextValue : next.getValue()) {
						if (b.length() == 0) {
							b.append('?');
						} else {
							b.append('&');
						}
						try {
							b.append(URLEncoder.encode(next.getKey(), "UTF-8"));
							b.append('=');
							b.append(URLEncoder.encode(nextValue, "UTF-8"));
						} catch (UnsupportedEncodingException e) {
							throw new ca.uhn.fhir.context.ConfigurationException("UTF-8 not supported", e);
						}
					}
				}
				return b.toString();
			} else if (theKey.startsWith("requestHeader.")) {
				String val = myRequest.getHeader(theKey.substring("requestHeader.".length()));
				return StringUtils.defaultString(val);
			} else if (theKey.startsWith("remoteAddr")) {
				return StringUtils.defaultString(myRequest.getRemoteAddr());
			} else if (theKey.equals("responseEncodingNoDefault")) {
				RestfulServerUtils.ResponseEncoding encoding = RestfulServerUtils.determineResponseEncodingNoDefault(
						myRequestDetails, myRequestDetails.getServer().getDefaultResponseEncoding());
				if (encoding != null) {
					return encoding.getEncoding().name();
				}
				return "";
			} else if (theKey.equals("exceptionMessage")) {
				return myException != null ? myException.getMessage() : null;
			} else if (theKey.equals("requestUrl")) {
				return myRequest.getRequestURL().toString();
			} else if (theKey.equals("requestVerb")) {
				return myRequest.getMethod();
			} else if (theKey.equals("requestBodyFhir")) {
				String contentType = myRequest.getContentType();
				if (isNotBlank(contentType)) {
					int colonIndex = contentType.indexOf(';');
					if (colonIndex != -1) {
						contentType = contentType.substring(0, colonIndex);
					}
					contentType = contentType.trim();

					EncodingEnum encoding = EncodingEnum.forContentType(contentType);
					if (encoding != null) {
						byte[] requestContents = myRequestDetails.loadRequestContents();
						return requestContents.toString();
					}
				}
				return "";
			} else if ("processingTimeMillis".equals(theKey)) {
				Date startTime = (Date) myRequest.getAttribute(RestfulServer.REQUEST_START_TIME);
				if (startTime != null) {
					long time = System.currentTimeMillis() - startTime.getTime();
					return Long.toString(time);
				}
			}
			return "!VAL!";
		}
	}

	private ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	@SuppressWarnings("unused")
	private void performTransform(OutputStream os, IBaseResource resource, String styleSheet) {

		// Input xml data file
		ClassLoader classLoader = getContextClassLoader();

		// Input xsl (stylesheet) file
		String xslInput = classLoader.getResource(styleSheet).getFile();
		log.debug("xslInput = " + xslInput);
		// Set the property to use xalan processor
		System.setProperty("javax.xml.transform.TransformerFactory",
				"org.apache.xalan.processor.TransformerFactoryImpl");

		// try with resources
		try {
			InputStream xml = new ByteArrayInputStream(
					ctx.newXmlParser().encodeResourceToString(resource).getBytes(StandardCharsets.UTF_8));

			// For Windows repace escape sequence
			xslInput = xslInput.replace("%20", " ");
			log.debug("open fileInputStream for xsl " + xslInput);
			FileInputStream xsl = new FileInputStream(xslInput);

			// Instantiate a transformer factory
			TransformerFactory tFactory = TransformerFactory.newInstance();

			// Use the TransformerFactory to process the stylesheet source and produce a
			// Transformer
			StreamSource styleSource = new StreamSource(xsl);
			Transformer transformer = tFactory.newTransformer(styleSource);

			// Use the transformer and perform the transformation
			StreamSource xmlSource = new StreamSource(xml);
			StreamResult result = new StreamResult(os);
			log.trace("Transforming");
			transformer.transform(xmlSource, result);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new InternalErrorException(ex.getMessage());
		}

	}
}
