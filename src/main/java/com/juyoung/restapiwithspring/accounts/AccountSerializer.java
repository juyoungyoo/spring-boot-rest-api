package com.juyoung.restapiwithspring.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

// object mapper @jsonComponent
public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account value,
                          JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeEndObject();
    }
}