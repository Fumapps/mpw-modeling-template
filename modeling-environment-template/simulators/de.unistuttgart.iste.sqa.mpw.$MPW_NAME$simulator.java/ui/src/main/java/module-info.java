module de.unistuttgart.$MPW_NAME$.ui {
    requires transitive de.unistuttgart.$MPW_NAME$.core;
    requires mpw.framework.utils;

    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;

    exports de.unistuttgart.$MPW_NAME$.ui;
    opens de.unistuttgart.$MPW_NAME$.ui;
    opens fxml;
    opens css;
    opens images;
}