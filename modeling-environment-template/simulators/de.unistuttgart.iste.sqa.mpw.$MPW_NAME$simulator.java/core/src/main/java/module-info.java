module de.unistuttgart.$MPW_NAME$.core {
    requires transitive mpw.framework.core;
    requires mpw.framework.utils;
    requires javafx.base;

    exports de.unistuttgart.$MPW_NAME$.facade;
    exports de.unistuttgart.$MPW_NAME$.$MPW_NAME$ to de.unistuttgart.$MPW_NAME$.main;
    exports de.unistuttgart.$MPW_NAME$.viewmodel.impl to de.unistuttgart.$MPW_NAME$.ui;

    opens de.unistuttgart.$MPW_NAME$.$MPW_NAME$;
    opens de.unistuttgart.$MPW_NAME$.$STAGE_NAME_PLURAL$;
}