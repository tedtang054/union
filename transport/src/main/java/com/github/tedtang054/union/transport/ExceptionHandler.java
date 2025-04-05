package com.github.tedtang054.union.transport;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @Author: dengJh
 * @Date: 2023/10/20 8:53
 */
@Slf4j
public class ExceptionHandler implements BiConsumer<Throwable, Object> {

    final static String AUTHENTICATE_TEMPLATE = "Digest realm=\"{0}\",nonce=\"{1}\"";

    @Override
    public void accept(Throwable error, Object o) {
//        if (error instanceof NoAuthorizedException) {
//            NoAuthorizedException exception = (NoAuthorizedException) error;
//            SipRequest request = exception.getRequest();
//            SessionManager.sendToPeer(registerUnauthorized(request));
//            return;
//        }
//        if (error instanceof LatencyMessageException) {
//            LatencyMessageException exception = (LatencyMessageException) error;
//            SipRequest request = exception.getRequest();
//            SipResponse failed = SipResponseHelper.failed(request, exception.getStatus(), null);
//            SessionManager.sendToPeer(failed);
//            return;
//        }
//        if (error instanceof UnrecognizedException) {
//            UnrecognizedException exception = (UnrecognizedException) error;
//            InetSocketAddress peer = exception.getPeer();
//            Object data = exception.getData();
//            if (data instanceof SipRequest) {
//                var failed = SipResponseHelper.failed((SipRequest) data, exception.getStatus());
//                SessionManager.sendToPeer(failed);
//                return;
//            }
//            SipResponse failed = SipResponseHelper.failed(exception.getStatus(), null, peer);
//            SessionManager.sendToPeer(failed);
//            return;
//        }
//        log.error("internal error : ", error);
    }

//    private SipResponse registerUnauthorized(SipRequest request) {
//        Map<String, String> headers = new HashMap<>();
//        SipHeader sipHeader = request.getSipHeader();
//        String deviceId = sipHeader.getDeviceId();
//        String nonce = request.getSipHeader().getNonce();
//        String authenticate = MessageFormat.format(AUTHENTICATE_TEMPLATE, deviceId, nonce);
//        headers.put(SipHeader.WWW_AUTHENTICATE, authenticate);
//        return SipResponseHelper.failed(request, SipStatus.UNAUTHORIZED, headers);
//    }

}
