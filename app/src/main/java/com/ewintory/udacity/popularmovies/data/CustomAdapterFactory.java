package com.ewintory.udacity.popularmovies.data;

import com.google.gson.TypeAdapterFactory;
import com.ryanharter.auto.value.gson.GsonTypeAdapterFactory;

/**
 * Фабрика для сериализации обьектов AutoValue
 *
 * @author Emin Yahyayev
 */
@GsonTypeAdapterFactory
public abstract class CustomAdapterFactory implements TypeAdapterFactory {

    // Static factory method to access the package
    // private generated implementation
    public static TypeAdapterFactory create() {
        return new AutoValueGson_CustomAdapterFactory();
    }

}
