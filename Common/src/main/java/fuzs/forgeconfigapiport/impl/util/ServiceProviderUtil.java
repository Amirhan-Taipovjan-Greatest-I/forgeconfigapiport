package fuzs.forgeconfigapiport.impl.util;

import java.util.ServiceLoader;

public final class ServiceProviderUtil {

    private ServiceProviderUtil() {

    }

    public static <T> T loadServiceProvider(Class<T> clazz) {
        return ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }
}