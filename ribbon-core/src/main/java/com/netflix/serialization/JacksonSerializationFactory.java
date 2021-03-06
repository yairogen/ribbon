/*
 *
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.netflix.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;

public class JacksonSerializationFactory implements SerializationFactory<ContentTypeBasedSerializerKey>{

    private static final JsonCodec instance = new JsonCodec();
    @Override
    public Optional<Deserializer> getDeserializer(ContentTypeBasedSerializerKey key) {
        if (key.getContentType().equalsIgnoreCase("application/json")) {
            return Optional.<Deserializer>of(instance);
        }
        return Optional.absent();
    }

    @Override
    public Optional<Serializer> getSerializer(ContentTypeBasedSerializerKey key) {
        if (key.getContentType().equalsIgnoreCase("application/json")) {
            return Optional.<Serializer>of(instance);
        }
        return Optional.absent();
    }

}

class JsonCodec implements Serializer, Deserializer {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T deserialize(InputStream in, TypeToken<T> type)
            throws IOException {
        return mapper.readValue(in, new TypeTokenBasedReference<T>(type));
    }
    
    @Override
    public void serialize(OutputStream out, Object object) throws IOException {
        mapper.writeValue(out, object);
    }
}

class TypeTokenBasedReference<T> extends TypeReference<T> {
    
    final Type type;
    public TypeTokenBasedReference(TypeToken<T> typeToken) {
        type = typeToken.getType();    
        
    }

    @Override
    public Type getType() {
        return type;
    }
}
