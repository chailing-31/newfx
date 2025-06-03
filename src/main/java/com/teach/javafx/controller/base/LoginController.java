package com.teach.javafx.controller.base;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.request.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.io.IOException;

/**
 * LoginController 登录交互控制类 对应 base/login-view.fxml
 *  @FXML  属性 对应fxml文件中的 fx:id 属性 如TextField usernameField 对应 fx:id="usernameField"
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性  如onLoginButtonClick() 对应onAction="#onLoginButtonClick"
 */
public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private VBox vbox;
    @FXML
    private Button loginButton;
//    @FXML
//    private Button studentLoginButton;
//    @FXML
//    private Button teacherLoginButton;

//    @FXML
//    protected void onStudentLoginClick() {
//        loadAndSwitchScene("student/student-view.fxml", "学生系统");
//    }
//
//    @FXML
//    protected void onTeacherLoginClick() {
//        loadAndSwitchScene("teacher/teacher-view.fxml", "教师系统");
//    }

    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
//        usernameField.setText("2022030001");
//        usernameField.setText("200799013517");
        usernameField.setText("admin");
        passwordField.setText("123456");
//        vbox.setId("min");  // id选择器 #
//        vbox.getStyleClass().add("min");  类选择器 .
        vbox.setStyle("-fx-background-image: url('shanda1.jpg'); -fx-background-repeat: no-repeat; -fx-background-size: cover;");  //inline选择器
//        loginButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
    }

    /**
     *  点击登录按钮 执行onLoginButtonClick 方法 从面板上获取用户名和密码，请求后台登录服务，登录成功加载主框架，切换舞台到主框架，登录不成功，提示错误信息
     */
    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        LoginRequest loginRequest = new LoginRequest(username,password);
        String msg = HttpRequestUtil.login(loginRequest);
        if(msg != null) {
            MessageDialog.showDialog( msg);
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("base/main-frame.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), -1, -1);
            AppStore.setMainFrameController((MainFrameController) fxmlLoader.getController());
            MainApplication.resetStage("教学管理系统", scene);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//
//    /**
//     * 通用方法：加载FXML并切换场景
//     */
//    private void loadAndSwitchScene(String fxmlPath, String title) {
//        try {
//            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("base/main-frame.fxml"));
//            Scene scene = new Scene(loader.load());
//            MainApplication.resetStage(title, scene);
//        } catch (IOException e) {
//            MessageDialog.showDialog("加载页面失败: " + e.getMessage());
//        }
//    }
}