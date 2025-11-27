package com.github.kitten_java.enderchest.persistent;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Array;

public final class SerializableArrayDataType<T extends ConfigurationSerializable> implements PersistentDataType<byte[], T[]> {
    private final Class<T> type;
    private final Class<T[]> types;

    public SerializableArrayDataType(Class<T[]> types) {
        this.type = (Class<T>) types.getComponentType();
        this.types = types;
    }

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<T[]> getComplexType() {
        return types;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull T[] serializable, @NotNull PersistentDataAdapterContext context) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream)) {
            bukkitObjectOutputStream.writeInt(serializable.length);
            for (final T t : serializable) {
                bukkitObjectOutputStream.writeObject(t);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(getExceptionMessage(type, SerializationType.SERIALIZATION), e);
        }
    }

    @Override
    public @NotNull T[] fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext context) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(inputStream)) {
            final T[] ts = (T[]) Array.newInstance(type, bukkitObjectInputStream.readInt());
            for (int i = 0; i < ts.length; i++) {
                ts[i] = (T) bukkitObjectInputStream.readObject();
            }
            return ts;
        } catch (final EOFException e) {
            return (T[]) Array.newInstance(getComplexType().getComponentType(), 0);
        } catch (final IOException e) {
            throw new UncheckedIOException(getExceptionMessage(type, SerializationType.DESERIALIZATION), e);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(getExceptionMessage(type, SerializationType.DESERIALIZATION), e);
        }
    }

    static String getExceptionMessage(Class<? extends ConfigurationSerializable> type, SerializationType serializationType) {
        return "Could not " + serializationType + " object of type " + type.getName() + ".";
    }

    enum SerializationType {
        SERIALIZATION("serialization"),
        DESERIALIZATION("deserialization");

        private final String fancyName;

        SerializationType(String fancyName) {
            this.fancyName = fancyName;
        }

        @Override
        public String toString() {
            return fancyName;
        }
    }
}
