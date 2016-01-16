package com.lealpoints.i18n;

public enum Message {
    COMMON_USER_ERROR(
            ":/ We are very sorry, there was an error. We're trying to solve it. " +
                    "You can email us to support@lealpoints.com if you want to be updated about this.",

            ":/ Los sentimos, hubo un error. Estamos tratando de resolverlo, " +
                    "puede enviarnos un correo a support@lealpoints.com si desea saber mas al respecto."
    ),
    LOGIN_FAILED(
            "Login failed! Please verify your information.",

            "No se pudo iniciar sesión, verifique su correo y contraseña."
    ),
    WE_HAVE_SENT_YOU_AND_ACTIVATION_LINK(
            "We've sent you an email. Open it up to activate your account. " +
                    "If you do not receive that email within 1 hour, please email support@lealpoints.com",

            "Se ha enviado un link de activación a su correo. Si no lo recibe dentro de una hora, " +
                    "favor de enviar un correo a support@lealpoints.com"
    ),
    EMAIL_ALREADY_EXISTS(
            "This email is already being used on Leal Points.",

            "Este correo ya se esta utilizando en Leal Points."),
    PASSWORD_AND_CONFIRMATION_ARE_DIFFERENT(
            "Password and confirmation are different.",

            "La contraseña y la confirmación son diferentes."),
    PASSWORD_MUST_HAVE_AT_LEAST_6_CHARACTERS(
            "Password must have at least 6 characters.",

            "La contraseña debe tener al menos seis caracteres."
    ),
    CLIENT_REGISTERED_SUCCESSFULLY(
            "The client was successfully added.",

            "El cliente fue registrado exitosamente."
    ),
    PHONE_MUST_HAVE_10_DIGITS(
            "Phone must have 10 digits.",

            "El número de teléfono debe tener al menos 10 dígitos."
    ),
    THE_CLIENT_ALREADY_EXISTS(
            "The client already exists.",

            "El cliente ya existe."
    ),
    CONFIGURATION_UPDATED(
            "Configuration updated.",

            "Configuración actualizada."
    ),
    PROMOTION_SUCCESSFULLY_ADDED(
            "Promotion successfully added.",

            "La promoción fue agregada exitosamente."
    ),
    POINTS_AWARDED(
            "Points awarded: %s",

            "Puntos otorgados: %s"
    ),
    SALE_KEY_ALREADY_EXISTS(
            "Sale key already exists.",

            "El número de venta ya existe."
    ),
    CLIENT_DOES_NOT_HAVE_AVAILABLE_PROMOTIONS(
            "Client does not have available promotions.",

            "El cliente no alcanza ninguna promoción."
    ),
    PHONE_NUMBER_DOES_NOT_EXIST(
            "Phone number does not exist.",

            "El número de teléfono no existe."
    ),
    COULD_NOT_READ_FILE(
            "Could not read file.",

            "No se pudo leer el archivo."
    ),
    YOUR_LOGO_WAS_UPDATED(
            "Your logo was updated.",

            "El logotipo fue actualizado."
    ),
    THE_CLIENT_DID_NOT_GET_POINTS(
            "The client did not get any points.",

            "El cliente no obtuvo puntos."
    ),
    YOUR_USER_IS_NOT_ACTIVE(
            "You user is not active, please verify your email and click on the activation link.",

            "Su usuario no esta activo, favor de verificar su correo y dar clic en el link de activación."
    ),
    YOUR_USER_HAS_BEEN_ACTIVATED(
            "Your user has been activated.",

            "Su usuario se ha activado."
    ),
    THIS_EMAIL_DOES_NOT_EXIST(
            "This email does not exist.",

            "El correo no existe."
    ),
    INVALID_LOGO_FILE(
            "Invalid file, only png or jpeg images are valid.",

            "Imagen inválida, solo imágenes jpeg o png son válidas."
    ),
    THE_PROMOTION_WAS_DELETED(
            "The promotion was deleted.",

            "La promoción fue eliminada."
    ),
    THE_PROMOTION_COULD_NOT_BE_DELETED(
            "The promotion could not be deleted.",

            "La promoción no pudo ser eliminada."
    ),
    WELCOME_TO_LEALPOINTS_YOUR_KEY_IS(
            "Welcome to Leal Points, your key is:",

            "Bienvenido a Leal Points, su clave es:"
    ),
    ACTIVATION_EMAIL_SUBJECT(
            "Leal Points activation.",

            "Activación de Leal Points."
    ),
    ACTIVATION_EMAIL_BODY(
            "Thank you for joining Leal Points. Click the link below to activate your account.",

            "Gracias por registrarse en Leal Points. Para activar su cuenta, haga clic en este link:"
    ),
    KEY_EMAIL_SMS_MESSAGE(
            "Thank you for joining Leal Points. This is your login key:",

            "Gracias por registrarse en Leal Points. Esta es su clave de acceso:"
    ),
    WE_HAVE_SENT_YOU_A_NEW_PASSWORD_TO_YOUR_EMAIL(
            "We have sent you a new password to your email.",

            "Se ha enviado una nueva contraseña a su correo."
    ),
    NEW_PASSWORD_EMAIL_SUBJECT(
            "Recover password.",

            "Recuperar contraseña."
    ),
    NEW_PASSWORD_EMAIL_BODY(
            "Your new password is:",

            "Su nueva contraseña es:"
    ),
    YOUR_PASSWORD_HAS_BEEN_CHANGED(
            "Your password has been changed.",

            "Su contraseña se ha cambiado."
    ),
    PROMOTION_APPLIED(
            "Promotion applied.",

            "Promoción aplicada."
    ),
    EMAIL_IS_EMPTY(
            "Please specify your email",

            "Ingresa tu correo electrónico"
    ),
    PASSWORD_IS_EMPTY(
            "Please specify your password",

            "Ingresa tu contraseña"
    ),
    MOBILE_APP_AD_MESSAGE(
            "You've got %s points at %s. Install Leal Points to see our promotions. %s",

            "Tienes %s puntos en %s. Instala Leal Points para ver las promociones. %s"
    ),
    MOBILE_APP_AD_MESSAGE_SENT_SUCCESSFULLY(
            "Mobile app ad message was sent successfully",

            "El mensaje de promoción de Leal Points fue enviado exitosamente."
    ),
    MOBILE_APP_AD_MESSAGE_WAS_NOT_SENT_SUCCESSFULLY(
            "There was an error while sending the mobile app ad message, try again.",

            "Ocurrió un error al enviar el mensaje de promoción de Leal Points, intente de nuevo."
    ),
    DEFAULT_PROMOTION_MESSAGE(
            "10%% off in your next purchase!",

            "10%% de descuento en su próxima compra!"
    ), EMAIL_IS_INVALID(
            "Specify a valid email",

            "Indique un email válido"
    ), COMPANY_NAME_IS_EMPTY(
            "Specify the company name",

            "Indique el nombre de la compañía"
    ), EMPTY_SALE_KEY(
            "You should specify a sale key",

            "Indique el número de venta."
    );

    private final String english;
    private final String spanish;

    Message(String english, String spanish) {
        this.english = english;
        this.spanish = spanish;
    }

    public String get(Language language) {
        switch (language.getLangId()) {
            case "en":
                return this.english;
            case "es":
                return this.spanish;
            default:
                return this.english;
        }
    }
}