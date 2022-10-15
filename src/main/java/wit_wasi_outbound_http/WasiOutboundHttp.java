package wit_wasi_outbound_http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.teavm.interop.Memory;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.Export;

public final class WasiOutboundHttp {
    private WasiOutboundHttp() {}
    
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
    @Import(name = "request", module = "wasi-outbound-http")
    private static native void wasmImportRequest(int p0, int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9, int p10);
    
    public static Result<Response, HttpError> request(Request req) {
        byte[] bytes = ((req).uri).getBytes(StandardCharsets.UTF_8);
        
        int address = Memory.malloc(((req).headers).size() * 16, 4).toInt();
        for (int index = 0; index < ((req).headers).size(); ++index) {
            Tuple2<String, String> element = ((req).headers).get(index);
            int base = address + (index * 16);
            byte[] bytes0 = ((element).f0).getBytes(StandardCharsets.UTF_8);
            Address.fromInt((base) + 4).putInt(bytes0.length);
            Address.fromInt((base) + 0).putInt(Address.ofData(bytes0).toInt());
            byte[] bytes1 = ((element).f1).getBytes(StandardCharsets.UTF_8);
            Address.fromInt((base) + 12).putInt(bytes1.length);
            Address.fromInt((base) + 8).putInt(Address.ofData(bytes1).toInt());
            
        }
        
        int address6 = Memory.malloc(((req).params).size() * 16, 4).toInt();
        for (int index7 = 0; index7 < ((req).params).size(); ++index7) {
            Tuple2<String, String> element2 = ((req).params).get(index7);
            int base3 = address6 + (index7 * 16);
            byte[] bytes4 = ((element2).f0).getBytes(StandardCharsets.UTF_8);
            Address.fromInt((base3) + 4).putInt(bytes4.length);
            Address.fromInt((base3) + 0).putInt(Address.ofData(bytes4).toInt());
            byte[] bytes5 = ((element2).f1).getBytes(StandardCharsets.UTF_8);
            Address.fromInt((base3) + 12).putInt(bytes5.length);
            Address.fromInt((base3) + 8).putInt(Address.ofData(bytes5).toInt());
            
        }
        
        int lowered;
        int lowered13;
        int lowered14;
        
        if (((req).body) == null) {
            
            lowered = 0;
            lowered13 = 0;
            lowered14 = 0;
            
        } else {
            byte[] payload12 = (byte[]) ((req).body);
            
            lowered = 1;
            lowered13 = Address.ofData(payload12).toInt();
            lowered14 = (payload12).length;
            
        }
        wasmImportRequest((req).method.ordinal(), Address.ofData(bytes).toInt(), bytes.length, address, ((req).headers).size(), address6, ((req).params).size(), lowered, lowered13, lowered14, RETURN_AREA);
        
        Result<Response, HttpError> lifted34;
        
        switch ((((int) Address.fromInt((RETURN_AREA) + 0).getByte()) & 0xFF)) {
            case 0: {
                
                ArrayList<Tuple2<String, String>> lifted;
                
                switch ((((int) Address.fromInt((RETURN_AREA) + 8).getByte()) & 0xFF)) {
                    case 0: {
                        lifted = null;
                        break;
                    }
                    
                    case 1: {
                        
                        ArrayList<Tuple2<String, String>> array = new ArrayList<>(Address.fromInt((RETURN_AREA) + 16).getInt());
                        for (int index25 = 0; index25 < (Address.fromInt((RETURN_AREA) + 16).getInt()); ++index25) {
                            int base22 = (Address.fromInt((RETURN_AREA) + 12).getInt()) + (index25 * 16);
                            
                            byte[] bytes23 = new byte[Address.fromInt((base22) + 4).getInt()];
                            Memory.getBytes(Address.fromInt(Address.fromInt((base22) + 0).getInt()), bytes23, 0, Address.fromInt((base22) + 4).getInt());
                            
                            byte[] bytes24 = new byte[Address.fromInt((base22) + 12).getInt()];
                            Memory.getBytes(Address.fromInt(Address.fromInt((base22) + 8).getInt()), bytes24, 0, Address.fromInt((base22) + 12).getInt());
                            
                            array.add(new Tuple2<String, String>(new String(bytes23, StandardCharsets.UTF_8), new String(bytes24, StandardCharsets.UTF_8)));
                        }
                        Memory.free(Address.fromInt(Address.fromInt((RETURN_AREA) + 12).getInt()), (Address.fromInt((RETURN_AREA) + 16).getInt()) * 16, 4);
                        
                        lifted = array;
                        break;
                    }
                    
                    default: throw new AssertionError("invalid discriminant: " + ((((int) Address.fromInt((RETURN_AREA) + 8).getByte()) & 0xFF)));
                }
                
                byte[] lifted31;
                
                switch ((((int) Address.fromInt((RETURN_AREA) + 20).getByte()) & 0xFF)) {
                    case 0: {
                        lifted31 = null;
                        break;
                    }
                    
                    case 1: {
                        
                        byte[] array30 = new byte[Address.fromInt((RETURN_AREA) + 28).getInt()];
                        Memory.getBytes(Address.fromInt(Address.fromInt((RETURN_AREA) + 24).getInt()), array30, 0, (array30).length);
                        
                        lifted31 = array30;
                        break;
                    }
                    
                    default: throw new AssertionError("invalid discriminant: " + ((((int) Address.fromInt((RETURN_AREA) + 20).getByte()) & 0xFF)));
                }
                
                lifted34 = Result.<Response, HttpError>ok(new Response((short) ((((int) Address.fromInt((RETURN_AREA) + 4).getShort()) & 0xFFFF)), lifted, lifted31));
                break;
            }
            case 1: {
                
                lifted34 = Result.<Response, HttpError>err(HttpError.values()[(((int) Address.fromInt((RETURN_AREA) + 4).getByte()) & 0xFF)]);
                break;
            }
            
            default: throw new AssertionError("invalid discriminant: " + ((((int) Address.fromInt((RETURN_AREA) + 0).getByte()) & 0xFF)));
        }
        Memory.free(Address.fromInt(address), ((req).headers).size() * 16, 4);
        Memory.free(Address.fromInt(address6), ((req).params).size() * 16, 4);
        return lifted34;
        
    }
    
    public static final class Tuple2<T0, T1> {
        public final T0 f0;
        public final T1 f1;
        
        public Tuple2(T0 f0, T1 f1) {
            this.f0 = f0;
            this.f1 = f1;
        }
        
    }
    
    public static final class Result<Ok, Err> {
        public final byte tag;
        private final Object value;
        
        private Result(byte tag, Object value) {
            this.tag = tag;
            this.value = value;
        }
        
        public static <Ok, Err> Result<Ok, Err> ok(Ok ok) {
            return new Result<>(OK, ok);
        }
        
        public static <Ok, Err> Result<Ok, Err> err(Err err) {
            return new Result<>(ERR, err);
        }
        
        public Ok getOk() {
            if (this.tag == OK) {
                return (Ok) this.value;
            } else {
                throw new RuntimeException("expected OK, got " + this.tag);
            }
        }
        
        public Err getErr() {
            if (this.tag == ERR) {
                return (Err) this.value;
            } else {
                throw new RuntimeException("expected ERR, got " + this.tag);
            }
        }
        
        public static final byte OK = 0;
        public static final byte ERR = 1;
    }
    private static final int RETURN_AREA = Memory.malloc(32, 4).toInt();
}
