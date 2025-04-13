module SchoolReportManager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.desktop;
    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires com.h2database;
    requires itextpdf;
    
    opens com.schoolreport.ui.controller to javafx.fxml;
    opens com.schoolreport.core.model to org.hibernate.orm.core, java.persistence;
    
    exports com.schoolreport.ui;
    exports com.schoolreport.core.model;
    exports com.schoolreport.core.service;
    exports com.schoolreport.service.impl;
    exports com.schoolreport.persistence.repository;
    exports com.schoolreport.persistence.util;
    exports com.schoolreport.export.util;
}