module org.jcnc.jnotepad {
    requires javafx.controls;
    // 不知道为什么，不加这个，日志框架在打包后的程序不起作用，会报错
    // Exception in thread "JavaFX Application Thread" java.lang.NoClassDefFoundError: javax/naming/NamingException
    // 但我打开源代码，他们的模块的确有包含这个，java.naming，这个没懂，我干脆自己导入
    requires java.naming;
    requires atlantafx.base;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires org.slf4j;
    requires ch.qos.logback.core;
    requires ch.qos.logback.classic;
    requires com.ibm.icu;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.antdesignicons;
    requires richtextfx.fat;
    requires java.desktop;
    requires org.commonmark;
    requires javafx.web;
    requires com.kodedu.terminalfx;

    exports org.jcnc.jnotepad;
    exports org.jcnc.jnotepad.model.enums;
    exports org.jcnc.jnotepad.app.config;
    exports org.jcnc.jnotepad.app.i18n;
    exports org.jcnc.jnotepad.common.constants;
    exports org.jcnc.jnotepad.controller.config;
    exports org.jcnc.jnotepad.controller.manager;
    exports org.jcnc.jnotepad.controller.i18n;
    exports org.jcnc.jnotepad.controller.event.handler.toolbar;
    exports org.jcnc.jnotepad.controller.event.handler.menuitem;
    exports org.jcnc.jnotepad.component.module.interfaces;
    opens org.jcnc.jnotepad.app.config;
    exports org.jcnc.jnotepad.plugin.interfaces;
    exports org.jcnc.jnotepad.views.root.bottom.function;

    exports org.jcnc.jnotepad.component.module;
    exports org.jcnc.jnotepad.model.entity;
    exports org.jcnc.jnotepad.views.root.bottom;
    exports org.jcnc.jnotepad.views.root.bottom.status;
    exports org.jcnc.jnotepad.api.core.views.sidebar.bottom;
    exports org.jcnc.jnotepad.api.core.controller.config;
    exports org.jcnc.jnotepad.component.module.base;
    exports org.jcnc.jnotepad.component.stage.setting;
    exports org.jcnc.jnotepad.component.module.vbox;
    exports org.jcnc.jnotepad.component.module.hbox;
    exports org.jcnc.jnotepad.component.stage.topmenu.help;
    exports org.jcnc.jnotepad.component.stage.topmenu.plugin;
    exports org.jcnc.jnotepad.component.module.vbox.components;

}