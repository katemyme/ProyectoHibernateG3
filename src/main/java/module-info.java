module ni.edu.uam.proyectohibernateg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;


    opens ni.edu.uam.proyectohibernateg3 to javafx.fxml;
    exports ni.edu.uam.proyectohibernateg3;
    exports ni.edu.uam.proyectohibernateg3.controller;
    opens ni.edu.uam.proyectohibernateg3.controller to javafx.fxml;
    // Hibernate accede a los campos de Libro y PropertyValueFactory lee sus getters, ambos por reflexión
    opens ni.edu.uam.proyectohibernateg3.model to org.hibernate.orm.core, javafx.base;
}
