package com.mirkoexample.Salvo;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
le doy la anotación @Restcontroller al index
el restcontroller es la fusion de de @Controller(2) y @Respondebody(1)
(1):toodo metodo puesto en esta etiqueta, spring, automaticamente convierte el return value y lo pasa al body del http
(2): es para crear un mapeo de modelo al objeto y encontrarle algun tipo de vista al user.
en resumen:
simplemente devuelve el objeto y los datos del objeto se escriben directamente en json o xml
 */
@RestController
public class IndexController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "Error handling";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
