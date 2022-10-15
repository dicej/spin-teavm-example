package wit_spin_http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.teavm.interop.Memory;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.Export;

public final class SpinHttp {
    private SpinHttp() {}
    
    public static enum HttpError {
        SUCCESS, DESTINATION_NOT_ALLOWED, INVALID_URL, REQUEST_ERROR, RUNTIME_ERROR, TOO_MANY_REQUESTS
    }
    
    public static enum Method {
        GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
    }
    
    public static final class Request {
        public final Method method;
        public final String uri;
        public final ArrayList<Tuple2<String, String>> headers;
        public final ArrayList<Tuple2<String, String>> params;
        public final byte[] body;
        
        public Request(Method method, String uri, ArrayList<Tuple2<String, String>> headers, ArrayList<Tuple2<String, String>> params, byte[] body) {
            this.method = method;
            this.uri = uri;
            this.headers = headers;
            this.params = params;
            this.body = body;
        }
    }
    
    public static final class Response {
        public final short status;
        public final ArrayList<Tuple2<String, String>> headers;
        public final byte[] body;
        
        public Response(short status, ArrayList<Tuple2<String, String>> headers, byte[] body) {
            this.status = status;
            this.headers = headers;
            this.body = body;
        }
    }
    
    @Export(name = "handle-http-request")
    private static int wasmExportHandleHttpRequest(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9) {
        
        byte[] bytes = new byte[p2];
        Memory.getBytes(Address.fromInt(p1), bytes, 0, p2);
        
        ArrayList<Tuple2<String, String>> array = new ArrayList<>(p4);
        for (int index = 0; index < (p4); ++index) {
            int base = (p3) + (index * 16);
            
            byte[] bytes0 = new byte[Address.fromInt((base) + 4).getInt()];
            Memory.getBytes(Address.fromInt(Address.fromInt((base) + 0).getInt()), bytes0, 0, Address.fromInt((base) + 4).getInt());
            
            byte[] bytes1 = new byte[Address.fromInt((base) + 12).getInt()];
            Memory.getBytes(Address.fromInt(Address.fromInt((base) + 8).getInt()), bytes1, 0, Address.fromInt((base) + 12).getInt());
            
            array.add(new Tuple2<String, String>(new String(bytes0, StandardCharsets.UTF_8), new String(bytes1, StandardCharsets.UTF_8)));
        }
        Memory.free(Address.fromInt(p3), (p4) * 16, 4);
        
        ArrayList<Tuple2<String, String>> array6 = new ArrayList<>(p6);
        for (int index7 = 0; index7 < (p6); ++index7) {
            int base3 = (p5) + (index7 * 16);
            
            byte[] bytes4 = new byte[Address.fromInt((base3) + 4).getInt()];
            Memory.getBytes(Address.fromInt(Address.fromInt((base3) + 0).getInt()), bytes4, 0, Address.fromInt((base3) + 4).getInt());
            
            byte[] bytes5 = new byte[Address.fromInt((base3) + 12).getInt()];
            Memory.getBytes(Address.fromInt(Address.fromInt((base3) + 8).getInt()), bytes5, 0, Address.fromInt((base3) + 12).getInt());
            
            array6.add(new Tuple2<String, String>(new String(bytes4, StandardCharsets.UTF_8), new String(bytes5, StandardCharsets.UTF_8)));
        }
        Memory.free(Address.fromInt(p5), (p6) * 16, 4);
        
        byte[] lifted;
        
        switch (p7) {
            case 0: {
                lifted = null;
                break;
            }
            
            case 1: {
                
                byte[] array12 = new byte[p9];
                Memory.getBytes(Address.fromInt(p8), array12, 0, (array12).length);
                
                lifted = array12;
                break;
            }
            
            default: throw new AssertionError("invalid discriminant: " + (p7));
        }
        
        Response result = SpinHttpImpl.handleHttpRequest(new Request(Method.values()[p0], new String(bytes, StandardCharsets.UTF_8), array, array6, lifted));
        
        Address.fromInt((RETURN_AREA) + 0).putShort((short) (((int) ((result).status)) & 0xFFFF));
        
        if (((result).headers) == null) {
            
            Address.fromInt((RETURN_AREA) + 4).putByte((byte) (0));
            
        } else {
            ArrayList<Tuple2<String, String>> payload17 = (ArrayList<Tuple2<String, String>>) ((result).headers);
            Address.fromInt((RETURN_AREA) + 4).putByte((byte) (1));
            
            int address23 = Memory.malloc((payload17).size() * 16, 4).toInt();
            for (int index24 = 0; index24 < (payload17).size(); ++index24) {
                Tuple2<String, String> element18 = (payload17).get(index24);
                int base19 = address23 + (index24 * 16);
                byte[] bytes20 = ((element18).f0).getBytes(StandardCharsets.UTF_8);
                
                Address address = Memory.malloc(bytes20.length, 1);
                Memory.putBytes(address, bytes20, 0, bytes20.length);
                Address.fromInt((base19) + 4).putInt(bytes20.length);
                Address.fromInt((base19) + 0).putInt(address.toInt());
                byte[] bytes21 = ((element18).f1).getBytes(StandardCharsets.UTF_8);
                
                Address address22 = Memory.malloc(bytes21.length, 1);
                Memory.putBytes(address22, bytes21, 0, bytes21.length);
                Address.fromInt((base19) + 12).putInt(bytes21.length);
                Address.fromInt((base19) + 8).putInt(address22.toInt());
                
            }
            Address.fromInt((RETURN_AREA) + 12).putInt((payload17).size());
            Address.fromInt((RETURN_AREA) + 8).putInt(address23);
            
        }
        
        if (((result).body) == null) {
            
            Address.fromInt((RETURN_AREA) + 16).putByte((byte) (0));
            
        } else {
            byte[] payload30 = (byte[]) ((result).body);
            Address.fromInt((RETURN_AREA) + 16).putByte((byte) (1));
            
            Address address31 = Memory.malloc(1 * (payload30).length, 1);
            Memory.putBytes(address31, payload30, 0, (payload30).length);
            Address.fromInt((RETURN_AREA) + 24).putInt((payload30).length);
            Address.fromInt((RETURN_AREA) + 20).putInt(address31.toInt());
            
        }
        return RETURN_AREA;
        
    }
    
    @Export(name = "cabi_post_handle-http-request")
    private static void wasmExportHandleHttpRequestPostReturn(int p0) {
        
        switch ((((int) Address.fromInt((p0) + 4).getByte()) & 0xFF)) {
            case 0: {
                
                break;
            }
            case 1: {
                
                for (int index = 0; index < (Address.fromInt((p0) + 12).getInt()); ++index) {
                    int base3 = (Address.fromInt((p0) + 8).getInt()) + (index * 16);
                    Memory.free(Address.fromInt(Address.fromInt((base3) + 0).getInt()), Address.fromInt((base3) + 4).getInt(), 1);
                    Memory.free(Address.fromInt(Address.fromInt((base3) + 8).getInt()), Address.fromInt((base3) + 12).getInt(), 1);
                    
                }
                Memory.free(Address.fromInt(Address.fromInt((p0) + 8).getInt()), (Address.fromInt((p0) + 12).getInt()) * 16, 4);
                
                break;
            }
        }
        
        switch ((((int) Address.fromInt((p0) + 16).getByte()) & 0xFF)) {
            case 0: {
                
                break;
            }
            case 1: {
                Memory.free(Address.fromInt(Address.fromInt((p0) + 20).getInt()), (Address.fromInt((p0) + 24).getInt()) * 1, 1);
                
                break;
            }
        }
        
    }
    
    public static final class Tuple2<T0, T1> {
        public final T0 f0;
        public final T1 f1;
        
        public Tuple2(T0 f0, T1 f1) {
            this.f0 = f0;
            this.f1 = f1;
        }
        
    }
    private static final int RETURN_AREA = Memory.malloc(28, 4).toInt();
}
