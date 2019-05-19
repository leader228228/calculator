package ua.edu.sumdu.nc.controllers;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.edu.sumdu.nc.beans.OperationRecord;
import ua.edu.sumdu.nc.dao.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class AllUserOperations {
    private final ApplicationContext applicationContext;
    private final DAO dao;

    public AllUserOperations(ApplicationContext applicationContext, DAO dao) {
        this.applicationContext = applicationContext;
        this.dao = dao;
    }

    @GetMapping(value = "/useroperations/{userId}")
    public ModelAndView getAllUserOperations(@PathVariable(name = "userId") int userId) {
        try (Connection connection = dao.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT  * FROM OPERATIONS WHERE USERID = ?");
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("allRecords", parseUsers(resultSet));
            modelAndView.setViewName("allRecords");
            modelAndView.addObject("userId", userId);
            return modelAndView;
        } catch (SQLException e) {
            return new ModelAndView("errorPage", "error", e.getClass().getSimpleName());
        }
    }

    private List<OperationRecord> parseUsers(ResultSet resultSet) {
        List<OperationRecord> operationRecords = new LinkedList<>();
        try {
            while (resultSet.next()) {
                OperationRecord operationRecord = applicationContext.getBean(OperationRecord.class);
                operationRecord.setUserId(resultSet.getInt("USERID"));
                operationRecord.setOne(resultSet.getDouble("ONE"));
                operationRecord.setTwo(resultSet.getDouble("TWO"));
                operationRecord.setOperation(resultSet.getString("OPERATION"));
                operationRecord.setResult(resultSet.getDouble("RESULT"));
                operationRecords.add(operationRecord);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return operationRecords;
    }
}
