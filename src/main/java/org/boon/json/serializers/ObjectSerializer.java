package org.boon.json.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public interface ObjectSerializer {

    void serializeObject(JsonSerializerInternal serializer,  Object instance, CharBuf builder );

}
