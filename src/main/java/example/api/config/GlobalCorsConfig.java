//package example.api.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//public class GlobalCorsConfig {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://192.168.55.208:8081"); // Allow only your front-end origin
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        config.addExposedHeader("Access-Control-Allow-Origin");
//        config.addExposedHeader("Access-Control-Allow-Credentials");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//}
