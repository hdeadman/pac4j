package org.pac4j.saml.logout.impl;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.impl.RequestAbstractTypeImpl;
import org.opensaml.saml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Build a SAML2 logout response.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class SAML2LogoutResponseBuilder {

    private String bindingType;

    private int issueInstantSkewSeconds = 0;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    public SAML2LogoutResponseBuilder(final String bindingType) {
        this.bindingType = bindingType;
    }

    public LogoutResponse build(final SAML2MessageContext context) {
        final SingleLogoutService ssoService = context.getIDPSingleLogoutService(this.bindingType);
        return buildLogoutResponse(context, ssoService);
    }

    @SuppressWarnings("unchecked")
    protected final LogoutResponse buildLogoutResponse(final SAML2MessageContext context,
                                                      final SingleLogoutService ssoService) {

        final SAMLObjectBuilder<LogoutResponse> builder = (SAMLObjectBuilder<LogoutResponse>) this.builderFactory
            .getBuilder(LogoutResponse.DEFAULT_ELEMENT_NAME);
        final LogoutResponse response = builder.buildObject();

        final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();

        response.setID(SAML2Utils.generateID());
        response.setIssuer(getIssuer(selfContext.getEntityId()));
        response.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(this.issueInstantSkewSeconds).toInstant());
        response.setVersion(SAMLVersion.VERSION_20);
        response.setDestination(ssoService.getLocation());
        response.setStatus(getSuccess());
        final SAMLObject originalMessage = (SAMLObject) context.getMessageContext().getMessage();
        if (originalMessage instanceof RequestAbstractTypeImpl) {
            response.setInResponseTo(((RequestAbstractTypeImpl) originalMessage).getID());
        }

        return response;
    }

    protected Status getSuccess() {
        final SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) this.builderFactory
            .getBuilder(Status.DEFAULT_ELEMENT_NAME);
        final Status status = statusBuilder.buildObject();
        final StatusCode statusCode = new StatusCodeBuilder().buildObject();
        statusCode.setValue(StatusCode.SUCCESS);
        status.setStatusCode(statusCode);
        return status;
    }

    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final String spEntityId) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
                .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        return issuer;
    }

    public void setBindingType(final String bindingType) {
        this.bindingType = bindingType;
    }

    public void setIssueInstantSkewSeconds(final int issueInstantSkewSeconds) {
        this.issueInstantSkewSeconds = issueInstantSkewSeconds;
    }
}
