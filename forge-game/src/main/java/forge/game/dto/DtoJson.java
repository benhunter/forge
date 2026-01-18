package forge.game.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public final class DtoJson {
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapterFactory(new EnumNameTypeAdapterFactory())
            .create();

    private DtoJson() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static String toJson(Object dto) {
        return GSON.toJson(dto);
    }

    private static final class EnumNameTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<? super T> rawType = type.getRawType();
            if (!Enum.class.isAssignableFrom(rawType)) {
                return null;
            }
            Class<? extends Enum> enumType = (Class<? extends Enum>) rawType;
            return (TypeAdapter<T>) new EnumNameTypeAdapter(enumType);
        }
    }

    private static final class EnumNameTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
        private final Class<T> enumType;

        private EnumNameTypeAdapter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(value.name());
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            return Enum.valueOf(enumType, in.nextString());
        }
    }
}
