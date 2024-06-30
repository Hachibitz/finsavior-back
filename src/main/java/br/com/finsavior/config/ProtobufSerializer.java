package br.com.finsavior.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.gson.Gson;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;

public class ProtobufSerializer extends JsonSerializer<Message> {

    @Override
    public void serialize(Message value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        /*Gson gson = new Gson();
        String jsonValue = gson.toJson(value);
        gen.writeRawValue(jsonValue);*/

        JsonFormat.Printer printer = JsonFormat.printer()
                .omittingInsignificantWhitespace();

        String jsonValue = printer.print(value);
        gen.writeRawValue(jsonValue);
    }
}
