package com.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

// Encapsulates search-related logic for loan applications


public class SearchController {

    @FXML private TextField searchField;
    @FXML private TableView<ApplicationRow> resultsTable;
    @FXML private TableColumn<ApplicationRow, Integer> colId;
    @FXML private TableColumn<ApplicationRow, String> colNic;
    @FXML private TableColumn<ApplicationRow, String> colFullName;
    @FXML private TableColumn<ApplicationRow, String> colPhone;
    @FXML private TableColumn<ApplicationRow, String> colEmail;
    @FXML private TableColumn<ApplicationRow, Double> colMonthlyIncome;
    @FXML private TableColumn<ApplicationRow, Double> colLoanAmount;
    @FXML private TableColumn<ApplicationRow, Integer> colLoanTerm;
    @FXML private TableColumn<ApplicationRow, String> colStatus;
    @FXML private TableColumn<ApplicationRow, Integer> colRisk;

    private static final String DB_URL = "jdbc:sqlite:database/loanshield.db";

    // METHOD: initialize() — Abstraction (UI setup hidden from outside calls)
    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colMonthlyIncome.setCellValueFactory(new PropertyValueFactory<>("monthlyIncome"));
        colLoanAmount.setCellValueFactory(new PropertyValueFactory<>("requestedLoanAmount"));
        colLoanTerm.setCellValueFactory(new PropertyValueFactory<>("loanTermYears"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("applicationStatus"));
        colRisk.setCellValueFactory(new PropertyValueFactory<>("riskScore"));

        // Apply light text cell factory for dark theme visibility
        applyLightTextCellFactory(colId);
        applyLightTextCellFactory(colNic);
        applyLightTextCellFactory(colFullName);
        applyLightTextCellFactory(colPhone);
        applyLightTextCellFactory(colEmail);
        applyLightTextCellFactory(colMonthlyIncome);
        applyLightTextCellFactory(colLoanAmount);
        applyLightTextCellFactory(colLoanTerm);
        applyLightTextCellFactory(colStatus);
        applyLightTextCellFactory(colRisk);
    }

    // Helper: Sets dark text color for table cells on light background
    private <T> void applyLightTextCellFactory(TableColumn<ApplicationRow, T> column) {
        column.setCellFactory(col -> new TableCell<ApplicationRow, T>() {
            private final Label label = new Label();
            {
                label.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 13px;");
            }
            
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(item.toString());
                    setGraphic(label);
                    setText(null);
                }
            }
        });
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String q = searchField.getText() == null ? "" : searchField.getText().trim();
        ObservableList<ApplicationRow> rows = FXCollections.observableArrayList();
        String sql = q.isEmpty() ?
                "SELECT id, nic, full_name, phone, email, monthly_income, requested_loan_amount, loan_term_years, application_status, risk_score FROM applications ORDER BY id DESC" :
                "SELECT id, nic, full_name, phone, email, monthly_income, requested_loan_amount, loan_term_years, application_status, risk_score FROM applications WHERE nic LIKE ? ORDER BY id DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!q.isEmpty()) {
                ps.setString(1, "%" + q + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new ApplicationRow(
                        rs.getInt("id"),
                        rs.getString("nic"),
                        rs.getString("full_name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getDouble("monthly_income"),
                        rs.getDouble("requested_loan_amount"),
                        rs.getInt("loan_term_years"),
                        rs.getString("application_status"),
                        rs.getInt("risk_score")
                ));
            }
            resultsTable.setItems(rows);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // METHOD: Handles navigation back — Abstraction of UI navigation logic
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Step1.fxml"));
            Parent step1Root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(step1Root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//encapsulation:..........

    
    public static class ApplicationRow {
        private final int id;
        private final String nic;
        private final String fullName;
        private final String phone;
        private final String email;
        private final double monthlyIncome;
        private final double requestedLoanAmount;
        private final int loanTermYears;
        private final String applicationStatus;
        private final int riskScore;


        // Constructor for ApplicationRow
//  CONSTRUCTOR: Initializes object state (Encapsulation)
        public ApplicationRow(int id, String nic, String fullName, String phone, String email,
                               double monthlyIncome, double requestedLoanAmount, int loanTermYears,
                               String applicationStatus, int riskScore) {
            this.id = id;
            this.nic = nic;
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.monthlyIncome = monthlyIncome;
            this.requestedLoanAmount = requestedLoanAmount;
            this.loanTermYears = loanTermYears;
            this.applicationStatus = applicationStatus;
            this.riskScore = riskScore;
        }

//  GETTERS: Provide controlled access to fields (Encapsulation)
        // Getters for ApplicationRow fields
        public int getId() { return id; }
        public String getNic() { return nic; }
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public double getMonthlyIncome() { return monthlyIncome; }
        public double getRequestedLoanAmount() { return requestedLoanAmount; }
        public int getLoanTermYears() { return loanTermYears; }
        public String getApplicationStatus() { return applicationStatus; }
        public int getRiskScore() { return riskScore; }
    }
}


