package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.*;
import javafx.scene.Scene;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StudentController 登录交互控制类 对应 student_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *
 * @FXML 属性 对应fxml文件中的
 * @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class StudentController extends ToolController {
    private ImageView photoImageView;
    @FXML
    private TableView<Map> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Map, String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Map, String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Map, String> deptColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Map, String> majorColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Map, String> classNameColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Map, String> cardColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Map, String> genderColumn; //学生信息表 性别列
    @FXML
    private TableColumn<Map, String> birthdayColumn; //学生信息表 出生日期列
    @FXML
    private TableColumn<Map, String> emailColumn; //学生信息表 邮箱列
    @FXML
    private TableColumn<Map, String> phoneColumn; //学生信息表 电话列
    @FXML
    private TableColumn<Map, String> addressColumn;//学生信息表 地址列
    @FXML
    private Button photoButton;  //照片显示和上传按钮

    @FXML
    private TextField numField; //学生信息  学号输入域
    @FXML
    private TextField nameField;  //学生信息  名称输入域
    @FXML
    private TextField deptField; //学生信息  院系输入域
    @FXML
    private TextField majorField; //学生信息  专业输入域
    @FXML
    private TextField classNameField; //学生信息  班级输入域
    @FXML
    private TextField cardField; //学生信息  证件号码输入域
    @FXML
    private ComboBox<OptionItem> genderComboBox;  //学生信息  性别输入域
    @FXML
    private DatePicker birthdayPick;  //学生信息  出生日期选择域
    @FXML
    private TextField emailField;  //学生信息  邮箱输入域
    @FXML
    private TextField phoneField;   //学生信息  电话输入域
    @FXML
    private TextField addressField;  //学生信息  地址输入域

    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer personId = null;  //当前编辑修改的学生的主键

    private ArrayList<Map> studentList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentList.size(); j++) {
            Map<String, Object> record = studentList.get(j);

            // 确保personId字段存在（可能是person_id或personId）
            if (record.containsKey("person_id") && !record.containsKey("personId")) {
                record.put("personId", record.get("person_id"));
            }

            // 确保className字段存在（可能是class_name或className）
            if (record.containsKey("class_name") && !record.containsKey("className")) {
                record.put("className", record.get("class_name"));
            }

            observableList.addAll(FXCollections.observableArrayList(record));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);
        photoButton.setGraphic(photoImageView);

        // 确保初始化时获取所有学生数据
        refreshStudentList();

        numColumn.setCellValueFactory(new MapValueFactory<>("num"));  //设置列值工程属性
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        deptColumn.setCellValueFactory(new MapValueFactory<>("dept"));
        majorColumn.setCellValueFactory(new MapValueFactory<>("major"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        cardColumn.setCellValueFactory(new MapValueFactory<>("card"));
        //genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        genderColumn.setCellValueFactory(cell -> {
            Map<String, Object> item = cell.getValue();
            String gender = CommonMethod.getString(item, "gender");
            String genderText = "1".equals(gender) ? "男" : "2".equals(gender) ? "女" : "";
            return new javafx.beans.property.SimpleStringProperty(genderText);
        });
        birthdayColumn.setCellValueFactory(new MapValueFactory<>("birthday"));
        emailColumn.setCellValueFactory(new MapValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");

        genderComboBox.getItems().addAll(genderList);
        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    /**
     * 清除学生表单中输入信息
     */
    public void clearPanel() {
        personId = null;
        numField.setText("");
        nameField.setText("");
        deptField.setText("");
        majorField.setText("");
        classNameField.setText("");
        cardField.setText("");
        genderComboBox.getSelectionModel().select(-1);
        birthdayPick.getEditor().setText("");
        emailField.setText("");
        phoneField.setText("");
        addressField.setText("");
        // 清除图片显示
        photoImageView.setImage(null);
    }

    protected void changeStudentInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentInfo", req);
        if (res == null) {
            MessageDialog.showDialog("无法连接到服务器，请检查网络连接");
            return;
        }
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        form = (Map) res.getData();
        numField.setText(CommonMethod.getString(form, "num"));
        nameField.setText(CommonMethod.getString(form, "name"));
        deptField.setText(CommonMethod.getString(form, "dept"));
        majorField.setText(CommonMethod.getString(form, "major"));
        classNameField.setText(CommonMethod.getString(form, "className"));
        cardField.setText(CommonMethod.getString(form, "card"));
        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
        birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
        emailField.setText(CommonMethod.getString(form, "email"));
        phoneField.setText(CommonMethod.getString(form, "phone"));
        addressField.setText(CommonMethod.getString(form, "address"));
        displayPhoto();
    }

    /**
     * 点击学生列表的某一行，根据personId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change) {
        changeStudentInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);
        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
            setTableViewData();
        }
    }

    /**
     * 刷新学生列表，强制从后台重新获取所有学生数据
     */
    public void refreshStudentList() {
        numNameTextField.setText(""); // 清空搜索条件

        try {
            // 使用API查询所有学生
            DataRequest req = new DataRequest();
            req.add("numName", "");
            DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

            if (res != null && res.getCode() == 0) {
                studentList = (ArrayList<Map>) res.getData();

                // 如果列表为空，可能是因为缓存问题，添加重试逻辑
                if (studentList.isEmpty()) {
                    try {
                        // 等待500ms后再次尝试
                        Thread.sleep(500);
                        res = HttpRequestUtil.request("/api/student/getStudentList", req);
                        if (res != null && res.getCode() == 0) {
                            studentList = (ArrayList<Map>) res.getData();
                        }
                    } catch (Exception e) {
                        // 忽略中断异常
                    }
                }

                // 强制清空并重新加载表视图
                setTableViewData();

                // 确保UI刷新
                dataTableView.refresh();
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                MessageDialog.showDialog("刷新学生列表失败: " + errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("刷新学生列表异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 简化的学生添加方法，专注于核心功能
     * 只要求填写学号和姓名，其他字段设置默认值
     */
    public void addSimpleStudent() {
        clearPanel();

        // 提示用户输入基本信息
        MessageDialog.showDialog("简化添加模式：请只填写学号和姓名，其他字段将使用默认值");

        // 预设一些默认值
        if (genderComboBox.getItems().size() > 0) {
            genderComboBox.getSelectionModel().select(0);
        }

        // 设置今天的日期
        java.time.LocalDate today = java.time.LocalDate.now();
        birthdayPick.setValue(today);

        // 设置默认院系和专业
        deptField.setText("软件学院");
        majorField.setText("软件工程");
        classNameField.setText("软工1班");
    }

    /**
     * 添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        addSimpleStudent();
    }

    /**
     * 点击删除按钮 删除当前编辑的学生的数据
     */
    @FXML
    protected void onDeleteButtonClick() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) {
            return;
        }
        personId = CommonMethod.getInteger(form, "personId");
        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/studentDelete", req);
        if (res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                onQueryButtonClick();
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
        } else {
            MessageDialog.showDialog("无法连接到服务器，请检查网络连接");
        }
    }

    /**
     * 验证学号是否已经存在
     *
     * @param num             要检查的学号
     * @param currentPersonId 当前学生ID（如果是编辑现有学生）
     * @return 如果学号不存在或者是当前编辑的学生的学号，返回true；否则返回false
     */
    private boolean validateStudentNum(String num, Integer currentPersonId) {
        DataRequest req = new DataRequest();
        req.add("numName", num);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);
        if (res != null && res.getCode() == 0) {
            ArrayList<Map> results = (ArrayList<Map>) res.getData();
            if (results.isEmpty()) {
                // 学号不存在，可以使用
                return true;
            } else {
                // 检查找到的学生是否是当前正在编辑的学生
                for (Map student : results) {
                    Integer foundPersonId = CommonMethod.getInteger(student, "personId");
                    if (foundPersonId != null && foundPersonId.equals(currentPersonId)) {
                        // 找到的是当前学生，可以使用
                        return true;
                    }
                }
                // 学号已被其他学生使用
                return false;
            }
        }
        // 无法验证，默认通过
        return true;
    }

    /**
     * 对字符串进行SQL安全转义，防止SQL注入
     *
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    private String sqlEscape(String input) {
        if (input == null) {
            return "NULL";
        }
        // 替换单引号为两个单引号，这是SQL中转义单引号的标准方法
        return "'" + input.replace("'", "''") + "'";
    }

    /**
     * 保存时强制创建完整学生记录（即使存在重复学号）
     */
    public void forceSaveCompleteStudent() {
        try {
            // 获取表单数据
            String num = numField.getText();
            String name = nameField.getText();

            // 检查必填字段
            if (num == null || num.trim().isEmpty()) {
                MessageDialog.showDialog("学号不能为空");
                return;
            }
            if (name == null || name.trim().isEmpty()) {
                MessageDialog.showDialog("姓名不能为空");
                return;
            }

            // 提示用户正在进行的操作
            MessageDialog.showDialog("正在创建完整学生记录，这将包含person表和student表的数据。\n请稍候...");

            // 检查学号是否已存在（使用API）
            DataRequest checkReq = new DataRequest();
            checkReq.add("numName", num); // 使用numName参数查询学号
            DataResponse checkRes = HttpRequestUtil.request("/api/student/getStudentList", checkReq);

            if (checkRes != null && checkRes.getCode() == 0) {
                ArrayList<Map> results = (ArrayList<Map>) checkRes.getData();
                if (results != null && !results.isEmpty()) {
                    StringBuilder sb = new StringBuilder("找到以下已存在的学号记录：\n\n");
                    for (Map record : results) {
                        String existingPersonId = CommonMethod.getString(record, "personId");
                        String existingNum = CommonMethod.getString(record, "num");
                        String existingName = CommonMethod.getString(record, "name");
                        sb.append("ID: ").append(existingPersonId)
                                .append(", 学号: ").append(existingNum)
                                .append(", 姓名: ").append(existingName)
                                .append("\n");
                    }

                    sb.append("\n需要先删除这些冲突记录才能创建新的完整学生记录。是否继续？");
                    int choice = MessageDialog.choiceDialog(sb.toString());

                    if (choice == MessageDialog.CHOICE_YES) {
                        // 使用标准API执行删除操作
                        for (Map record : results) {
                            Integer existingPersonId = CommonMethod.getInteger(record, "personId");
                            if (existingPersonId != null) {
                                // 使用标准删除API
                                DataRequest deleteReq = new DataRequest();
                                deleteReq.add("personId", existingPersonId);
                                DataResponse deleteRes = HttpRequestUtil.request("/api/student/studentDelete",
                                        deleteReq);

                                if (deleteRes == null || deleteRes.getCode() != 0) {
                                    String errorMsg = (deleteRes != null) ? deleteRes.getMsg() : "无法连接到服务器";
                                    MessageDialog.showDialog("删除记录失败: " + errorMsg);
                                    return;
                                }
                            }
                        }

                        MessageDialog.showDialog("已删除冲突记录，现在将创建新的完整学生记录");
                    } else {
                        MessageDialog.showDialog("操作已取消");
                        return;
                    }
                }
            }

            // 获取其他表单数据
            String dept = deptField.getText();
            String major = majorField.getText();
            String className = classNameField.getText();
            String card = cardField.getText();
            String gender = (genderComboBox.getSelectionModel() != null &&
                    genderComboBox.getSelectionModel().getSelectedItem() != null)
                    ? genderComboBox.getSelectionModel().getSelectedItem().getValue()
                    : null;
            String birthday = birthdayPick.getEditor().getText();
            String email = emailField.getText();
            String phone = phoneField.getText();
            String address = addressField.getText();

            // 使用标准API创建学生记录
            Map<String, Object> form = new HashMap<>();
            form.put("num", num);
            form.put("name", name);
            form.put("dept", dept);
            form.put("major", major);
            form.put("className", className);
            form.put("card", card);
            form.put("gender", gender);
            form.put("birthday", birthday);
            form.put("email", email);
            form.put("phone", phone);
            form.put("address", address);
            form.put("type", "1"); // 确保类型设置为学生

            // 第一步：尝试通过标准API创建记录
            DataRequest req = new DataRequest();
            req.add("personId", null); // 确保personId为null，表示新建
            req.add("form", form);

            // 添加重试逻辑
            DataResponse res = null;
            boolean success = false;
            int retryCount = 0;
            int maxRetries = 3;

            while (!success && retryCount < maxRetries) {
                try {
                    res = HttpRequestUtil.request("/api/student/studentEditSave", req);
                    success = true;
                } catch (Exception e) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        MessageDialog.showDialog("网络连接错误，请检查网络后重试");
                        return;
                    }
                    try {
                        // 等待一秒后重试
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (res != null && res.getCode() == 0) {
                // 获取新创建的personId
                Integer newPersonId = CommonMethod.getIntegerFromObject(res.getData());

                if (newPersonId != null) {
                    personId = newPersonId;

                    // 第二步：验证student表记录是否已创建
                    DataRequest infoReq = new DataRequest();
                    infoReq.add("personId", newPersonId);
                    DataResponse infoRes = HttpRequestUtil.request("/api/student/getStudentInfo", infoReq);

                    boolean studentRecordExists = (infoRes != null && infoRes.getCode() == 0);

                    // 第三步：如果没有student记录，直接通过API重新提交一次，确保student表更新
                    if (!studentRecordExists) {
                        MessageDialog.showDialog("person记录已创建，但student记录可能未正确创建，尝试再次提交...");

                        // 重新提交，这次带上personId
                        DataRequest updateReq = new DataRequest();
                        updateReq.add("personId", newPersonId);
                        updateReq.add("form", form);
                        DataResponse updateRes = HttpRequestUtil.request("/api/student/studentEditSave", updateReq);

                        if (updateRes != null && updateRes.getCode() == 0) {
                            MessageDialog.showDialog("成功创建完整学生记录！\nperson_id: " + newPersonId);
                        } else {
                            String errorMsg = (updateRes != null) ? updateRes.getMsg() : "无法连接到服务器";
                            MessageDialog.showDialog("创建student记录时出错: " + errorMsg);
                        }
                    } else {
                        MessageDialog.showDialog("成功创建完整学生记录！\nperson_id: " + newPersonId);
                    }

                    // 确保表视图刷新 - 多次尝试刷新方式
                    onQueryButtonClick();
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    } // 小延迟确保API响应
                    refreshStudentList();
                } else {
                    MessageDialog.showDialog("创建学生记录成功，但未返回有效的ID。请刷新列表查看。");
                    refreshStudentList();
                }
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                MessageDialog.showDialog("创建学生记录失败: " + errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
     */
    public void doNew() {
        clearPanel();
    }

    public void doSave() {
        onSaveButtonClick();
    }

    public void doDelete() {
        onDeleteButtonClick();
    }

    /**
     * 导出学生信息表的示例 重写ToolController 中的doExport 这里给出了一个导出学生基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */
    public void doExport() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName", numName);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/student/getStudentListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("前选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if (file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    protected void onImportButtonClick() {
        // 首先显示文件格式说明
        String formatInfo = "学生数据Excel文件格式说明：\n\n" +
                "1. 文件必须包含以下列：\n" +
                "   - 学号（必填）\n" +
                "   - 姓名（必填）\n" +
                "   - 院系\n" +
                "   - 专业\n" +
                "   - 班级\n" +
                "   - 性别（请使用：男/女）\n" +
                "   - 出生日期（格式：yyyy-MM-dd）\n" +
                "   - 证件号码\n" +
                "   - 邮箱\n" +
                "   - 电话\n" +
                "   - 地址\n\n" +
                "2. 第一行应为标题行\n" +
                "3. 学号不能重复\n" +
                "4. 邮箱必须符合格式要求\n\n" +
                "请选择操作：\n" +
                "1. 是 - 选择Excel文件导入\n" +
                "2. 否 - 下载示例模板文件";

        int choice = MessageDialog.choiceDialog(formatInfo);
        if (choice == MessageDialog.CHOICE_NO) {
            // 下载示例模板文件
            downloadExampleTemplate("student");
            return;
        } else if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("选择学生数据表");
        fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel 文件", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("CSV 文件", "*.csv"));
        File file = fileDialog.showOpenDialog(null);

        // 检查文件是否选择
        if (file == null) {
            return;
        }

        // 检查文件格式和大小
        String fileName = file.getName().toLowerCase();
        long fileSize = file.length();

        if (fileSize == 0) {
            MessageDialog.showDialog("文件为空，请选择有效的Excel文件");
            return;
        }

        if (!(fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv"))) {
            MessageDialog.showDialog("文件格式不正确，请选择Excel(.xlsx/.xls)或CSV(.csv)文件");
            return;
        }

        // 添加文件大小限制
        if (fileSize > 10 * 1024 * 1024) { // 10MB限制
            MessageDialog.showDialog("文件过大，请选择小于10MB的Excel文件");
            return;
        }

        // 验证Excel文件的有效性
        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                byte[] header = new byte[8];
                int bytesRead = fis.read(header);

                if (bytesRead >= 4) {
                    // 验证XLSX文件头 (ZIP格式: 50 4B 03 04)
                    if (fileName.endsWith(".xlsx")) {
                        if (!(header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04)) {
                            MessageDialog.showDialog(
                                    "文件格式错误：这不是一个有效的Excel(.xlsx)文件。\n\n请确保：\n1. 文件确实是Excel格式\n2. 文件没有损坏\n3. 尝试在Excel中重新保存文件");
                            return;
                        }
                    }
                    // 验证XLS文件头 (OLE格式: D0 CF 11 E0)
                    else if (fileName.endsWith(".xls")) {
                        if (!(header[0] == (byte) 0xD0 && header[1] == (byte) 0xCF && header[2] == 0x11
                                && header[3] == (byte) 0xE0)) {
                            MessageDialog.showDialog(
                                    "文件格式错误：这不是一个有效的Excel(.xls)文件。\n\n建议：\n1. 将.xls文件在Excel中打开\n2. 另存为.xlsx格式\n3. 使用保存的.xlsx文件进行导入");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                MessageDialog.showDialog("文件读取失败：" + e.getMessage() + "\n\n请检查文件是否被其他程序占用");
                return;
            }
        }

        // 显示进度提示
        MessageDialog.showDialog("正在导入学生数据，请稍候...");

        try {
            String paras = "";
            DataResponse res = HttpRequestUtil.importData("/api/student/importStudentData", file.getPath(), paras);

            if (res == null) {
                MessageDialog.showDialog("导入失败：无法连接到服务器，请检查网络连接");
                return;
            }

            if (res.getCode() == 0) {
                // 尝试获取导入数据的详细信息
                Map<String, Object> resultData = null;
                try {
                    resultData = (Map<String, Object>) res.getData();
                } catch (Exception e) {
                    // 忽略类型转换错误
                }

                // 构建导入成功消息
                StringBuilder successMsg = new StringBuilder("学生数据导入成功！");
                if (resultData != null) {
                    Integer totalRecords = CommonMethod.getInteger(resultData, "totalRecords");
                    if (totalRecords != null) {
                        successMsg.append("\n\n共导入 ").append(totalRecords).append(" 条学生记录。");
                    }
                }

                MessageDialog.showDialog(successMsg.toString());

                // 刷新学生列表
                refreshStudentList();
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器，请检查网络连接";

                // 增加更多用户友好的错误提示
                if (errorMsg.contains("格式") || errorMsg.contains("OOXML")) {
                    errorMsg = "文件格式不正确。请确保使用正确的Excel模板，包含必要的列（如学号、姓名等）。\n\n" +
                            "如果您使用的是CSV文件，请先在Excel中打开并另存为.xlsx格式。";
                } else if (errorMsg.contains("学号已经存在")) {
                    errorMsg = "发现重复的学号。系统不允许导入重复学号的学生数据。";
                } else if (errorMsg.contains("Illegal character") || errorMsg.contains("特殊字符")) {
                    errorMsg = "文件名包含特殊字符，请重命名文件后再尝试导入（建议使用英文和数字命名）。";
                }

                MessageDialog.showDialog("导入失败：" + errorMsg + "\n\n请检查文件格式是否符合要求，可以下载示例模板作为参考。");
            }
        } catch (Exception e) {
            MessageDialog.showDialog("导入过程中发生错误: " + e.getMessage() + "\n\n请确保文件名不包含特殊字符，建议使用英文和数字命名文件。");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onFamilyButtonClick() {
        if (personId == null) {
            MessageDialog.showDialog("请先选择一个学生");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        DataResponse res = HttpRequestUtil.request("/api/student/getFamilyMemberList", req);
        if (res.getCode() != 0) {
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        List<Map> familyList = (List<Map>) res.getData();
        ObservableList<Map> oList = FXCollections.observableArrayList(familyList);
        Scene scene = null, pScene = null;
        Stage stage;
        stage = new Stage();
        TableView<Map> table = new TableView<>(oList);
        table.setEditable(true);

        // 关系列
        TableColumn<Map, String> relationColumn = new TableColumn<>("关系");
        relationColumn.setCellValueFactory(new MapValueFactory("relation"));
        relationColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        relationColumn.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Map tempEntity = (Map) tempTable.getItems().get(event.getTablePosition().getRow());
            tempEntity.put("relation", event.getNewValue());
        });
        table.getColumns().add(relationColumn);

        // 姓名列
        TableColumn<Map, String> nameColumn = new TableColumn<>("姓名");
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        nameColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Map tempEntity = (Map) tempTable.getItems().get(event.getTablePosition().getRow());
            tempEntity.put("name", event.getNewValue());
        });
        table.getColumns().add(nameColumn);

        // 性别列
        TableColumn<Map, String> genderColumn = new TableColumn<>("性别");
        genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        genderColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        genderColumn.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Map tempEntity = (Map) tempTable.getItems().get(event.getTablePosition().getRow());
            tempEntity.put("gender", event.getNewValue());
        });
        table.getColumns().add(genderColumn);

        // 年龄列
        TableColumn<Map, String> ageColumn = new TableColumn<>("年龄");
        ageColumn.setCellValueFactory(new MapValueFactory<>("age"));
        ageColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        ageColumn.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Map tempEntity = (Map) tempTable.getItems().get(event.getTablePosition().getRow());
            tempEntity.put("age", event.getNewValue());
        });
        table.getColumns().add(ageColumn);

        // 单位列
        TableColumn<Map, String> unitColumn = new TableColumn<>("单位");
        unitColumn.setCellValueFactory(new MapValueFactory<>("unit"));
        unitColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        unitColumn.setOnEditCommit(event -> {
            TableView tempTable = event.getTableView();
            Map tempEntity = (Map) tempTable.getItems().get(event.getTablePosition().getRow());
            tempEntity.put("unit", event.getNewValue());
        });
        table.getColumns().add(unitColumn);

        // 创建框架布局
        BorderPane root = new BorderPane();
        root.setCenter(table);

        // 创建顶部说明
        Label instructionLabel = new Label("双击单元格可以直接编辑，点击保存按钮提交更改");
        instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        // 创建底部按钮区域
        FlowPane buttonPane = new FlowPane();
        buttonPane.setHgap(10);
        buttonPane.setAlignment(javafx.geometry.Pos.CENTER);
        buttonPane.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // 添加新成员按钮
        Button addButton = new Button("添加成员");
        addButton.setOnAction(e -> {
            // 显示家庭成员添加对话框
            showAddFamilyMemberDialog(oList, table);
        });

        // 删除选中成员按钮
        Button deleteButton = new Button("删除所选");
        deleteButton.setOnAction(event -> {
            Map selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                oList.remove(selectedItem);
                // 确保UI更新
                table.refresh();
            } else {
                MessageDialog.showDialog("请先选择一个家庭成员");
            }
        });

        // 保存按钮
        Button saveButton = new Button("保存");
        saveButton.setOnAction(event -> {
            // 提交所有变更
            saveFamilyMembers(personId, oList);
            stage.close();
        });

        // 取消按钮
        Button cancelButton = new Button("取消");
        cancelButton.setOnAction(event -> {
            stage.close();
        });

        // 添加按钮到布局
        buttonPane.getChildren().addAll(addButton, deleteButton, saveButton, cancelButton);

        // 设置布局
        VBox topBox = new VBox(10);
        topBox.setPadding(new javafx.geometry.Insets(10, 10, 5, 10));
        topBox.getChildren().add(instructionLabel);

        root.setTop(topBox);
        root.setBottom(buttonPane);

        // 创建场景
        scene = new Scene(root, 500, 400);
        stage.initOwner(MainApplication.getMainStage());
        stage.initModality(Modality.WINDOW_MODAL); // 使用WINDOW_MODAL而不是NONE
        stage.setAlwaysOnTop(true); // 确保窗口总是在最前面
        stage.setScene(scene);
        stage.setTitle("家庭成员信息管理");
        stage.setOnCloseRequest(event -> {
            MainApplication.setCanClose(true);
        });

        stage.showAndWait();
    }

    /**
     * 保存所有家庭成员信息
     */
    private void saveFamilyMembers(Integer personId, ObservableList<Map> familyMembers) {
        if (personId == null) {
            MessageDialog.showDialog("学生ID不能为空");
            return;
        }

        try {
            // 验证数据
            boolean hasError = false;
            for (Map member : familyMembers) {
                String name = CommonMethod.getString(member, "name");
                String relation = CommonMethod.getString(member, "relation");

                if ((name == null || name.trim().isEmpty()) &&
                        (relation == null || relation.trim().isEmpty())) {
                    // 如果姓名和关系都为空，删除这一行
                    member.put("_shouldDelete", true);
                } else if (name == null || name.trim().isEmpty()) {
                    hasError = true;
                    MessageDialog.showDialog("家庭成员姓名不能为空");
                    return;
                } else if (relation == null || relation.trim().isEmpty()) {
                    hasError = true;
                    MessageDialog.showDialog("家庭成员关系不能为空");
                    return;
                }
            }

            // 过滤掉标记为删除的空行
            List<Map> validMembers = new ArrayList<>();
            for (Map member : familyMembers) {
                Boolean shouldDelete = (Boolean) member.get("_shouldDelete");
                if (shouldDelete == null || !shouldDelete) {
                    validMembers.add(member);
                }
            }

            if (!hasError) {
                // 创建请求对象
                DataRequest req = new DataRequest();
                req.add("personId", personId);
                req.add("familyList", validMembers);

                // 发送保存请求 - 修改API路径
                DataResponse res = HttpRequestUtil.request("/api/student/saveFamilyMembers", req);

                if (res != null && res.getCode() == 0) {
                    MessageDialog.showDialog("家庭成员信息保存成功！");
                } else {
                    String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器，请检查网络连接";
                    MessageDialog.showDialog("保存失败: " + errorMsg);
                }
            }
        } catch (Exception e) {
            MessageDialog.showDialog("保存家庭成员信息时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void displayPhoto(){
        if (personId == null) {
            photoImageView.setImage(null);
            return;
        }

        DataRequest req = new DataRequest();
        req.add("fileName", "photo/" + personId + ".jpg");  //个人照片显示
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);
        if (bytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Image img = new Image(in);
            photoImageView.setImage(img);
        } else {
            // 如果找不到图片，清除之前的图片显示
            photoImageView.setImage(null);
        }
    }

    @FXML
    public void onPhotoButtonClick(){
        if (personId == null) {
            MessageDialog.showDialog("请先选择一个学生才能上传照片");
            return;
        }

        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("图片上传");
//        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
        File file = fileDialog.showOpenDialog(null);
        if(file == null)
            return;
        DataResponse res = HttpRequestUtil.uploadFile("/api/base/uploadPhoto", file.getPath(),
                "photo/" + personId + ".jpg");
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            displayPhoto();
        }
        else {
            String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器，请检查网络连接";
            MessageDialog.showDialog(errorMsg);
        }
    }
    @FXML
    public void onImportFeeButtonClick(){
        if (personId == null) {
            MessageDialog.showDialog("请先选择一个学生才能导入消费记录");
            return;
        }

        // 首先显示文件格式说明
        String formatInfo = "消费记录Excel文件格式说明：\n\n" +
                "1. 文件必须包含以下列：\n" +
                "   - 日期（格式：yyyy-MM-dd）\n" +
                "   - 金额（数字格式）\n" +
                "   - 消费类型（文本）\n" +
                "   - 消费地点（文本）\n\n" +
                "2. 第一行应为标题行\n" +
                "3. 日期必须有效\n" +
                "4. 金额必须为正数\n\n" +
                "请选择操作：\n" +
                "1. 是 - 选择Excel文件导入\n" +
                "2. 否 - 下载示例模板文件";

        int choice = MessageDialog.choiceDialog(formatInfo);
        if (choice == MessageDialog.CHOICE_NO) {
            // 下载示例模板文件
            downloadExampleTemplate("fee");
            return;
        } else if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        try {
            FileChooser fileDialog = new FileChooser();
            fileDialog.setTitle("选择消费数据表");
            fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));
            fileDialog.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel 文件", "*.xlsx", "*.xls"),
                    new FileChooser.ExtensionFilter("CSV 文件", "*.csv"));
            File file = fileDialog.showOpenDialog(null);
            if (file == null)
                return;

            // 检查文件格式和大小
            String fileName = file.getName().toLowerCase();
            long fileSize = file.length();

            if (fileSize == 0) {
                MessageDialog.showDialog("文件为空，请选择有效的Excel文件");
                return;
            }

            if (!(fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv"))) {
                MessageDialog.showDialog("文件格式不正确，请选择Excel(.xlsx/.xls)或CSV(.csv)文件");
                return;
            }

            // 显示进度提示
            MessageDialog.showDialog("正在上传消费数据，请稍候...");

            // 构建参数字符串，确保正确编码
            String paras = "personId=" + personId;

            // 使用修改后的导入方法
            DataResponse res = HttpRequestUtil.importData("/api/student/importFeeData", file.getPath(), paras);

            if (res == null) {
                MessageDialog.showDialog("导入失败：无法连接到服务器，请检查网络连接");
                return;
            }

            if (res.getCode() == 0) {
                // 尝试获取导入数据的详细信息
                Map<String, Object> resultData = null;
                try {
                    resultData = (Map<String, Object>) res.getData();
                } catch (Exception e) {
                    // 忽略类型转换错误
                }

                // 构建导入成功消息
                StringBuilder successMsg = new StringBuilder("消费数据导入成功！");
                if (resultData != null) {
                    Integer totalRecords = CommonMethod.getInteger(resultData, "totalRecords");
                    if (totalRecords != null) {
                        successMsg.append("\n\n共导入 ").append(totalRecords).append(" 条消费记录。");
                    }

                    String timeRange = CommonMethod.getString(resultData, "timeRange");
                    if (timeRange != null && !timeRange.isEmpty()) {
                        successMsg.append("\n时间范围: ").append(timeRange);
                    }

                    Double totalAmount = CommonMethod.getDouble(resultData, "totalAmount");
                    if (totalAmount != null) {
                        successMsg.append("\n总消费金额: ¥").append(String.format("%.2f", totalAmount));
                    }
                }

                MessageDialog.showDialog(successMsg.toString());
            } else {
                String errorMsg = res.getMsg();

                // 增加更多用户友好的错误提示
                if (errorMsg.contains("格式") || errorMsg.contains("OOXML") || errorMsg.contains("No valid entries")) {
                    MessageDialog.showDialog("文件格式错误！\n\n可能的原因：\n" +
                            "1. 文件不是有效的Excel格式\n" +
                            "2. 文件在传输过程中损坏\n" +
                            "3. 文件是.xls格式，请转换为.xlsx格式\n\n" +
                            "解决方案：\n" +
                            "• 在Excel中打开文件，另存为.xlsx格式\n" +
                            "• 确保文件包含正确的列：日期、金额等\n" +
                            "• 检查文件是否有密码保护或特殊格式\n\n" +
                            "原始错误：" + errorMsg);
                } else if (errorMsg.contains("权限")) {
                    errorMsg = "没有足够的权限导入数据，请联系管理员。";
                } else if (errorMsg.contains("重复")) {
                    errorMsg = "发现重复的消费记录。系统不允许导入重复数据。";
                } else if (errorMsg.contains("Illegal character") || errorMsg.contains("特殊字符")) {
                    errorMsg = "文件名包含特殊字符，请重命名文件后再尝试导入（建议使用英文和数字命名）。";
                } else {
                    MessageDialog.showDialog("导入失败：" + errorMsg + "\n\n请检查文件格式是否符合要求，可以下载示例模板作为参考。");
                    return;
                }

                MessageDialog.showDialog(errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("导入过程中发生错误: " + e.getMessage() + "\n\n请确保文件名不包含特殊字符，建议使用英文和数字命名文件。");
            e.printStackTrace();
        }
    }

    /**
     * 下载示例模板文件
     *
     * @param templateType 模板类型：student(学生导入模板) 或 fee(消费记录导入模板)
     */
    private void downloadExampleTemplate(String templateType) {
        try {
            // 创建请求获取示例模板
            DataRequest req = new DataRequest();
            req.add("type", templateType);

            String apiEndpoint = "/api/base/getExampleTemplate";
            byte[] bytes = HttpRequestUtil.requestByteData(apiEndpoint, req);

            if (bytes != null) {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("保存示例模板文件");
                fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));

                // 设置默认文件名
                String defaultFileName = templateType.equals("student") ? "学生数据导入模板.xlsx" : "消费记录导入模板.xlsx";
                fileDialog.setInitialFileName(defaultFileName);

                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Excel 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);

                if (file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                    MessageDialog.showDialog("示例模板已保存到：" + file.getPath() + "\n\n请使用此模板格式准备数据。");
                }
            } else {
                // 如果服务器没有提供模板，则生成本地模板
                generateLocalExampleTemplate(templateType);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("获取示例模板失败: " + e.getMessage());
            e.printStackTrace();

            // 尝试生成本地模板作为备选方案
            generateLocalExampleTemplate(templateType);
        }
    }

    /**
     * 生成本地示例模板文件（当服务器无法提供模板时使用）
     *
     * @param templateType 模板类型
     */
    private void generateLocalExampleTemplate(String templateType) {
        try {
            FileChooser fileDialog = new FileChooser();
            fileDialog.setTitle("保存示例模板文件");
            fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));

            // 设置默认文件名
            String defaultFileName = templateType.equals("student") ? "学生数据导入模板.xlsx" : "消费记录导入模板.xlsx";
            fileDialog.setInitialFileName(defaultFileName);

            fileDialog.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel 文件", "*.xlsx"));
            File file = fileDialog.showSaveDialog(null);

            if (file != null) {
                // 首先尝试创建Excel模板
                boolean excelCreated = createExcelTemplate(templateType, file);

                if (excelCreated) {
                    // 成功创建Excel模板
                    MessageDialog.showDialog("Excel格式示例模板已保存到：" + file.getPath() +
                            "\n\n请按照此模板填写数据后导入。");
                    return;
                }

                // 如果Excel创建失败，回退到CSV格式
                StringBuilder content = new StringBuilder();

                if (templateType.equals("student")) {
                    content.append("学号,姓名,院系,专业,班级,性别,出生日期,证件号码,邮箱,电话,地址\n");
                    content.append(
                            "2022030001,张三,软件学院,软件工程,软工1班,男,2001-01-01,370123200101010001,zhangsan@example.com,13800138000,山东省济南市\n");
                    content.append(
                            "2022030002,李四,软件学院,软件工程,软工1班,女,2001-02-02,370123200102020002,lisi@example.com,13800138001,山东省青岛市\n");
                } else {
                    content.append("日期,金额,消费类型,消费地点,备注\n");
                    content.append("2023-01-01,10.50,餐饮,学生食堂,早餐\n");
                    content.append("2023-01-01,15.00,餐饮,学生食堂,午餐\n");
                    content.append("2023-01-01,12.50,餐饮,学生食堂,晚餐\n");
                    content.append("2023-01-02,5.50,超市,校内超市,日用品\n");
                }

                // 写入文件
                FileOutputStream out = new FileOutputStream(file);
                out.write(content.toString().getBytes());
                out.close();

                MessageDialog.showDialog("CSV格式示例模板已保存到：" + file.getPath() +
                        "\n\n注意：这是一个CSV格式模板，实际导入时请将其在Excel中打开，并另存为Excel格式(.xlsx)后使用。" +
                        "\n此步骤十分重要，否则可能导致导入失败！");
            }
        } catch (Exception e) {
            MessageDialog.showDialog("生成示例模板失败: " + e.getMessage() +
                    "\n\n原因可能是：" + (e.getCause() != null ? e.getCause().getMessage() : "未知"));
            e.printStackTrace();
        }
    }

    /**
     * 测试服务器连接状态
     */
    public void testServerConnection() {
        try {
            DataRequest req = new DataRequest();
            req.add("testParam", "connection_test");
            DataResponse res = HttpRequestUtil.request("/api/base/ping", req);

            if (res != null) {
                if (res.getCode() == 0) {
                    MessageDialog.showDialog("服务器连接正常！");
                } else {
                    MessageDialog.showDialog("服务器返回错误: " + res.getMsg());
                }
            } else {
                MessageDialog.showDialog("无法连接到服务器，请检查网络和服务器状态。");
            }
        } catch (Exception e) {
            MessageDialog.showDialog("连接测试发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 诊断并显示学号问题
     */
    public void diagnoseStudentNumIssue(String num) {
        DataRequest req = new DataRequest();
        req.add("numName", num);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

        if (res != null && res.getCode() == 0) {
            ArrayList<Map> results = (ArrayList<Map>) res.getData();
            if (results.isEmpty()) {
                MessageDialog.showDialog("诊断结果: 学号 " + num + " 在当前视图中不存在。");
            } else {
                StringBuilder sb = new StringBuilder("诊断结果: 学号 " + num + " 已存在于数据库中。\n\n匹配的学生信息:\n");
                for (Map student : results) {
                    sb.append("ID: ").append(CommonMethod.getString(student, "personId"))
                            .append(", 姓名: ").append(CommonMethod.getString(student, "name"))
                            .append(", 学号: ").append(CommonMethod.getString(student, "num"))
                            .append("\n");
                }
                MessageDialog.showDialog(sb.toString());
            }
        } else {
            String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器，请检查网络连接";
            MessageDialog.showDialog("诊断失败: " + errorMsg);
        }
    }

    /**
     * 高级诊断学号问题 - 直接查询数据库
     */
    public void advancedDiagnoseStudentNum(String num) {
        try {
            // 创建一个特殊请求用于直接查询数据库
            DataRequest req = new DataRequest();
            req.add("sql", "SELECT p.person_id, p.num, p.name, p.type FROM person p WHERE p.num = '" + num + "'");
            DataResponse res = HttpRequestUtil.request("/api/system/executeQuery", req);

            if (res != null && res.getCode() == 0) {
                List<Map> results = (List<Map>) res.getData();
                if (results != null && !results.isEmpty()) {
                    StringBuilder sb = new StringBuilder("高级诊断结果:\n\n在person表中找到以下记录:\n");
                    for (Map record : results) {
                        sb.append("person_id: ").append(CommonMethod.getString(record, "person_id"))
                                .append(", 学号: ").append(CommonMethod.getString(record, "num"))
                                .append(", 姓名: ").append(CommonMethod.getString(record, "name"))
                                .append(", 类型: ").append(CommonMethod.getString(record, "type"))
                                .append("\n");
                    }

                    // 进一步检查student表
                    sb.append("\n检查student表中的关联记录:\n");
                    for (Map record : results) {
                        String personId = CommonMethod.getString(record, "person_id");
                        DataRequest studentReq = new DataRequest();
                        studentReq.add("sql", "SELECT * FROM student WHERE person_id = " + personId);
                        DataResponse studentRes = HttpRequestUtil.request("/api/system/executeQuery", studentReq);

                        if (studentRes != null && studentRes.getCode() == 0) {
                            List<Map> studentResults = (List<Map>) studentRes.getData();
                            if (studentResults != null && !studentResults.isEmpty()) {
                                sb.append("找到对应的student记录, person_id = ").append(personId).append("\n");
                            } else {
                                sb.append("未找到对应的student记录, person_id = ").append(personId).append("\n");
                            }
                        }
                    }

                    MessageDialog.showDialog(sb.toString());
                } else {
                    MessageDialog.showDialog("高级诊断结果: 数据库person表中没有找到学号为 " + num
                            + " 的记录。\n\n这可能意味着:\n1. 应用程序缓存与数据库不同步\n2. 有另一个表中有此学号\n3. 判断逻辑存在问题");
                }
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                MessageDialog.showDialog("高级诊断失败: " + errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("高级诊断异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 点击诊断学号按钮，进行当前输入学号的高级诊断
     */
    @FXML
    protected void onDiagnoseStudentNumButtonClick() {
        String num = numField.getText();
        if (num == null || num.trim().isEmpty()) {
            MessageDialog.showDialog("请先输入要诊断的学号");
            return;
        }

        advancedDiagnoseStudentNum(num);
    }

    /**
     * 查询所有person表记录（包括未关联student表的记录）
     */
    public void queryAllPersons() {
        try {
            // 创建请求，直接查询所有person记录
            DataRequest req = new DataRequest();
            req.add("sql", "SELECT p.person_id, p.num, p.name, p.type, p.dept, p.gender FROM person p ORDER BY p.num");
            DataResponse res = HttpRequestUtil.request("/api/system/executeQuery", req);

            if (res != null && res.getCode() == 0) {
                List<Map> results = (List<Map>) res.getData();
                if (results != null && !results.isEmpty()) {
                    // 将结果转换为可在界面显示的格式
                    ArrayList<Map> allPersonsList = new ArrayList<>();
                    for (Map record : results) {
                        Map<String, Object> personMap = new HashMap<>();
                        personMap.put("personId", CommonMethod.getString(record, "person_id"));
                        personMap.put("num", CommonMethod.getString(record, "num"));
                        personMap.put("name", CommonMethod.getString(record, "name"));
                        personMap.put("type", CommonMethod.getString(record, "type"));
                        personMap.put("dept", CommonMethod.getString(record, "dept"));
                        personMap.put("genderName", getGenderName(CommonMethod.getString(record, "gender")));
                        personMap.put("status", "仅person表记录");

                        // 查询是否有对应的student记录
                        String personId = CommonMethod.getString(record, "person_id");
                        DataRequest studentReq = new DataRequest();
                        studentReq.add("sql", "SELECT * FROM student WHERE person_id = " + personId);
                        DataResponse studentRes = HttpRequestUtil.request("/api/system/executeQuery", studentReq);

                        if (studentRes != null && studentRes.getCode() == 0) {
                            List<Map> studentResults = (List<Map>) studentRes.getData();
                            if (studentResults != null && !studentResults.isEmpty()) {
                                personMap.put("status", "完整学生记录");
                                // 如果有student记录，添加student表的信息
                                Map studentRecord = studentResults.get(0);
                                personMap.put("major", CommonMethod.getString(studentRecord, "major"));
                                personMap.put("className", CommonMethod.getString(studentRecord, "class_name"));
                            }
                        }

                        allPersonsList.add(personMap);
                    }

                    // 更新界面显示
                    studentList = allPersonsList;
                    setTableViewData();
                    MessageDialog.showDialog("成功查询到 " + results.size() + " 条person记录");
                } else {
                    MessageDialog.showDialog("数据库中未找到任何person记录");
                }
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                MessageDialog.showDialog("查询失败: " + errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("查询异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据性别代码获取性别名称
     */
    private String getGenderName(String genderCode) {
        if (genderCode == null)
            return "未知";

        for (OptionItem item : genderList) {
            if (genderCode.equals(item.getValue())) {
                return item.getTitle();
            }
        }
        return "未知";
    }

    /**
     * 清理不完整的person记录（仅在person表中存在但在student表中不存在的记录）
     * 注意：此操作会直接从数据库删除记录，请谨慎使用
     */
    public void cleanupIncompletePersons() {
        try {
            // 获取所有学生记录
            DataRequest req = new DataRequest();
            req.add("numName", "");
            DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

            if (res != null && res.getCode() == 0) {
                ArrayList<Map> studentList = (ArrayList<Map>) res.getData();

                StringBuilder sb = new StringBuilder("找到 ").append(studentList.size()).append(" 条学生记录。\n\n");
                sb.append("是否要对所有学生记录进行检查和清理？\n这将检查学生数据的完整性。");

                int choice = MessageDialog.choiceDialog(sb.toString());

                if (choice == MessageDialog.CHOICE_YES) {
                    int successCount = 0;
                    int failCount = 0;

                    for (Map student : studentList) {
                        try {
                            Integer personId = CommonMethod.getInteger(student, "personId");
                            if (personId != null) {
                                // 获取详细学生信息
                                DataRequest infoReq = new DataRequest();
                                infoReq.add("personId", personId);
                                DataResponse infoRes = HttpRequestUtil.request("/api/student/getStudentInfo", infoReq);

                                if (infoRes != null && infoRes.getCode() == 0) {
                                    // 记录存在且完整，不需要操作
                                    successCount++;
                                } else {
                                    // 记录不完整，需要删除重建
                                    DataRequest deleteReq = new DataRequest();
                                    deleteReq.add("personId", personId);
                                    DataResponse deleteRes = HttpRequestUtil.request("/api/student/studentDelete",
                                            deleteReq);

                                    if (deleteRes != null && deleteRes.getCode() == 0) {
                                        successCount++;
                                    } else {
                                        failCount++;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            failCount++;
                        }
                    }

                    MessageDialog.showDialog("清理完成！成功：" + successCount + "，失败：" + failCount);
                    refreshStudentList();
                }
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                MessageDialog.showDialog("获取学生列表失败: " + errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("清理异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 强制创建完整的学生记录（同时确保在person和student表中都有记录）
     */
    public void forceCreateCompleteStudent() {
        try {
            // 清除面板，确保personId为null
            clearPanel();

            // 提示用户
            MessageDialog.showDialog("请填写必要的学生信息，系统将强制创建完整的学生记录。");

            // 预设一些默认值，便于用户填写
            if (genderComboBox.getItems().size() > 0) {
                genderComboBox.getSelectionModel().select(0);
            }
            java.time.LocalDate today = java.time.LocalDate.now();
            birthdayPick.setValue(today);
            deptField.setText("软件学院");
            majorField.setText("软件工程");
            classNameField.setText("软工1班");
        } catch (Exception e) {
            MessageDialog.showDialog("操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * "新建完整学生"按钮点击事件
     */
    @FXML
    protected void onNewCompleteStudentButtonClick() {
        clearPanel();
        // 显示说明信息
        MessageDialog.showDialog("请输入新学生信息，然后点击【强制保存】按钮");
    }

    /**
     * "强制保存"按钮点击事件
     */
    @FXML
    protected void onForceSaveButtonClick() {
        forceSaveCompleteStudent();
    }

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
    @FXML
    protected void onSaveButtonClick() {
        if (numField.getText().isEmpty()) {
            MessageDialog.showDialog("学号不能为空");
            return;
        }

        // 获取表单数据
        String num = numField.getText();
        String name = nameField.getText();
        String dept = deptField.getText();
        String major = majorField.getText();
        String className = classNameField.getText();
        String card = cardField.getText();
        String gender = (genderComboBox.getSelectionModel() != null &&
                genderComboBox.getSelectionModel().getSelectedItem() != null)
                ? genderComboBox.getSelectionModel().getSelectedItem().getValue()
                : null;
        String birthday = birthdayPick.getEditor().getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        // 检查必填字段
        if (name == null || name.trim().isEmpty()) {
            MessageDialog.showDialog("姓名不能为空");
            return;
        }

        // 验证邮箱格式
        if (email != null && !email.trim().isEmpty()) {
            // 简单邮箱格式验证
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                MessageDialog.showDialog("邮箱格式不正确，请输入有效的电子邮件地址");
                return;
            }
        }

        // 创建请求对象
        Map<String, Object> form = new HashMap<>();
        form.put("num", num);
        form.put("name", name);
        form.put("dept", dept);
        form.put("major", major);
        form.put("className", className);
        form.put("card", card);
        form.put("gender", gender);
        form.put("birthday", birthday);
        form.put("email", email);
        form.put("phone", phone);
        form.put("address", address);

        DataRequest req = new DataRequest();
        req.add("personId", personId);
        req.add("form", form);

        // 添加重试逻辑
        DataResponse res = null;
        boolean success = false;
        int retryCount = 0;
        int maxRetries = 3;

        while (!success && retryCount < maxRetries) {
            try {
                res = HttpRequestUtil.request("/api/student/studentEditSave", req);
                success = true;
            } catch (Exception e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    MessageDialog.showDialog("网络连接错误，请检查网络后重试");
                    return;
                }
                try {
                    // 等待一秒后重试
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (res != null && res.getCode() == 0) {
            personId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            // 刷新学生列表，确保显示最新数据
            refreshStudentList();
        } else {
            String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器，请检查网络连接";

            // 处理错误消息
            if (errorMsg != null) {
                if (errorMsg.contains("邮件地址") || errorMsg.contains("Email")) {
                    MessageDialog.showDialog("邮箱格式不正确，请输入有效的电子邮件地址");
                } else if (errorMsg.contains("学号已经存在")) {
                    int choice = MessageDialog.choiceDialog(
                            "系统检测到学号 " + num + " 已经存在。\n请选择操作：\n1. 是 - 查看详细诊断信息\n2. 否 - 执行高级数据库诊断\n3. 取消");
                    if (choice == MessageDialog.CHOICE_YES) {
                        diagnoseStudentNumIssue(num);
                    } else if (choice == MessageDialog.CHOICE_NO) {
                        advancedDiagnoseStudentNum(num);
                    }
                } else {
                    MessageDialog.showDialog(errorMsg);
                }
            } else {
                MessageDialog.showDialog("保存失败，请检查输入数据是否正确");
            }
        }
    }

    /**
     * 诊断学生记录
     */
    public void diagnoseMySQLStudents() {
        try {
            StringBuilder report = new StringBuilder("学生记录诊断报告:\n\n");

            // 使用API查询所有学生记录
            DataRequest req = new DataRequest();
            req.add("numName", ""); // 空字符串查询所有学生
            DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

            if (res != null && res.getCode() == 0) {
                ArrayList<Map> studentList = (ArrayList<Map>) res.getData();
                report.append("学生记录总数: ").append(studentList.size()).append("\n\n");

                // 显示前20条记录
                int limit = Math.min(20, studentList.size());
                report.append("前").append(limit).append("条学生记录:\n");

                for (int i = 0; i < limit; i++) {
                    Map student = studentList.get(i);
                    report.append(i + 1).append(". ID: ").append(CommonMethod.getString(student, "personId"))
                            .append(", 学号: ").append(CommonMethod.getString(student, "num"))
                            .append(", 姓名: ").append(CommonMethod.getString(student, "name"))
                            .append(", 专业: ").append(CommonMethod.getString(student, "major"))
                            .append("\n");
                }

                // 检查是否有重复学号
                Map<String, List<Map>> numGroups = new HashMap<>();
                for (Map student : studentList) {
                    String num = CommonMethod.getString(student, "num");
                    if (num != null && !num.isEmpty()) {
                        if (!numGroups.containsKey(num)) {
                            numGroups.put(num, new ArrayList<>());
                        }
                        numGroups.get(num).add(student);
                    }
                }

                report.append("\n重复学号检查:\n");
                boolean hasDuplicates = false;
                for (Map.Entry<String, List<Map>> entry : numGroups.entrySet()) {
                    if (entry.getValue().size() > 1) {
                        hasDuplicates = true;
                        report.append("学号 '").append(entry.getKey()).append("' 出现了 ")
                                .append(entry.getValue().size()).append(" 次:\n");

                        for (Map student : entry.getValue()) {
                            report.append("  - ID: ").append(CommonMethod.getString(student, "personId"))
                                    .append(", 姓名: ").append(CommonMethod.getString(student, "name"))
                                    .append("\n");
                        }
                    }
                }

                if (!hasDuplicates) {
                    report.append("未检测到重复学号。\n");
                }

                // 添加刷新建议
                report.append("\n如果您刚刚添加了新学生但没有显示，请尝试:\n");
                report.append("1. 点击\"查询\"按钮刷新学生列表\n");
                report.append("2. 重启应用程序\n");
                report.append("3. 检查新添加学生的学号是否与现有学生冲突\n");

            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                report.append("无法查询学生记录: ").append(errorMsg);
            }

            MessageDialog.showDialog(report.toString());

        } catch (Exception e) {
            MessageDialog.showDialog("诊断异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 修复不完整的学生记录 - 针对只有person表没有student表记录的情况
     */
    public void fixIncompleteStudentRecords() {
        try {
            StringBuilder log = new StringBuilder("开始修复不完整学生记录...\n\n");

            // 查询所有学生记录
            DataRequest req = new DataRequest();
            req.add("numName", "");
            DataResponse res = HttpRequestUtil.request("/api/student/getStudentList", req);

            if (res != null && res.getCode() == 0) {
                ArrayList<Map> studentList = (ArrayList<Map>) res.getData();

                int totalCount = studentList.size();
                int fixedCount = 0;
                int errorCount = 0;

                log.append("找到 ").append(totalCount).append(" 个学生记录\n");
                log.append("开始检查每个学生是否有完整的student表记录...\n\n");

                for (Map student : studentList) {
                    Integer personId = CommonMethod.getInteger(student, "personId");
                    String num = CommonMethod.getString(student, "num");
                    String name = CommonMethod.getString(student, "name");

                    if (personId != null) {
                        log.append("检查学生: ").append(name).append(" (").append(num).append("), ID: ").append(personId)
                                .append("\n");

                        // 检查是否有详细信息 (student表记录)
                        DataRequest infoReq = new DataRequest();
                        infoReq.add("personId", personId);
                        DataResponse infoRes = HttpRequestUtil.request("/api/student/getStudentInfo", infoReq);

                        // 如果获取详细信息失败，说明可能缺少student表记录
                        if (infoRes == null || infoRes.getCode() != 0) {
                            log.append("  - 发现不完整记录! 尝试修复...\n");

                            // 构建完整表单用于更新
                            Map<String, Object> form = new HashMap<>();

                            // 将必要的字段存入form
                            form.put("num", num);
                            form.put("name", name);
                            form.put("major", "软件工程"); // 默认专业
                            form.put("className", "软工1班"); // 默认班级

                            // 从现有记录复制其他可能存在的字段
                            if (student.containsKey("dept"))
                                form.put("dept", student.get("dept"));
                            if (student.containsKey("gender"))
                                form.put("gender", student.get("gender"));
                            if (student.containsKey("birthday"))
                                form.put("birthday", student.get("birthday"));
                            if (student.containsKey("card"))
                                form.put("card", student.get("card"));
                            if (student.containsKey("email"))
                                form.put("email", student.get("email"));
                            if (student.containsKey("phone"))
                                form.put("phone", student.get("phone"));
                            if (student.containsKey("address"))
                                form.put("address", student.get("address"));

                            // 保存完整学生记录
                            DataRequest updateReq = new DataRequest();
                            updateReq.add("personId", personId);
                            updateReq.add("form", form);
                            DataResponse updateRes = HttpRequestUtil.request("/api/student/studentEditSave", updateReq);

                            if (updateRes != null && updateRes.getCode() == 0) {
                                log.append("  - 修复成功!\n");
                                fixedCount++;
                            } else {
                                String errorMsg = (updateRes != null) ? updateRes.getMsg() : "无法连接到服务器";
                                log.append("  - 修复失败: ").append(errorMsg).append("\n");
                                errorCount++;
                            }
                        } else {
                            log.append("  - 记录完整，无需修复\n");
                        }
                    }
                }

                log.append("\n修复总结:\n");
                log.append("总检查记录: ").append(totalCount).append("\n");
                log.append("修复记录数: ").append(fixedCount).append("\n");
                log.append("失败记录数: ").append(errorCount).append("\n");

                if (fixedCount > 0) {
                    log.append("\n刷新学生列表...");
                    refreshStudentList();
                }

                MessageDialog.showDialog(log.toString());
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "无法连接到服务器";
                MessageDialog.showDialog("获取学生列表失败: " + errorMsg);
            }
        } catch (Exception e) {
            MessageDialog.showDialog("修复操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 显示添加家庭成员对话框
     *
     * @param familyList 家庭成员列表
     * @param table      家庭成员表格
     */
    private void showAddFamilyMemberDialog(ObservableList<Map> familyList, TableView<Map> table) {
        // 创建对话框
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("添加家庭成员");
        dialog.setMinWidth(400);
        dialog.setMinHeight(300);
        dialog.setAlwaysOnTop(true); // 确保窗口总是在最前面
        dialog.initOwner(MainApplication.getMainStage()); // 设置父窗口

        // 创建表单元素
        Label relationLabel = new Label("关系:");
        TextField relationField = new TextField();

        Label nameLabel = new Label("姓名:");
        TextField nameField = new TextField();

        Label genderLabel = new Label("性别:");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("男", "女");
        genderComboBox.setValue("男");

        Label ageLabel = new Label("年龄:");
        TextField ageField = new TextField();

        Label unitLabel = new Label("单位:");
        TextField unitField = new TextField();

        // 创建按钮
        Button confirmButton = new Button("确认");
        confirmButton.setOnAction(event -> {
            // 验证输入
            if (relationField.getText().trim().isEmpty()) {
                MessageDialog.showDialog("请输入家庭成员关系");
                return;
            }
            if (nameField.getText().trim().isEmpty()) {
                MessageDialog.showDialog("请输入家庭成员姓名");
                return;
            }

            // 创建新的家庭成员记录
            Map<String, Object> newMember = new HashMap<>();
            newMember.put("relation", relationField.getText().trim());
            newMember.put("name", nameField.getText().trim());
            newMember.put("gender", genderComboBox.getValue());
            newMember.put("age", ageField.getText().trim());
            newMember.put("unit", unitField.getText().trim());
            newMember.put("familyId", null);

            // 添加到表格数据中
            familyList.add(newMember);

            // 确保UI更新
            table.refresh();

            // 选择新添加的行
            table.getSelectionModel().select(familyList.size() - 1);

            // 滚动到新行
            table.scrollTo(familyList.size() - 1);

            // 关闭对话框
            dialog.close();
        });

        Button cancelButton = new Button("取消");
        cancelButton.setOnAction(event -> dialog.close());

        // 创建表单布局
        GridPane formGrid = new GridPane();
        formGrid.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        formGrid.setVgap(10);
        formGrid.setHgap(10);

        formGrid.add(relationLabel, 0, 0);
        formGrid.add(relationField, 1, 0);
        formGrid.add(nameLabel, 0, 1);
        formGrid.add(nameField, 1, 1);
        formGrid.add(genderLabel, 0, 2);
        formGrid.add(genderComboBox, 1, 2);
        formGrid.add(ageLabel, 0, 3);
        formGrid.add(ageField, 1, 3);
        formGrid.add(unitLabel, 0, 4);
        formGrid.add(unitField, 1, 4);

        // 创建按钮布局
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().addAll(confirmButton, cancelButton);
        buttonBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // 创建主布局
        VBox mainLayout = new VBox(20);
        mainLayout.getChildren().addAll(
                new Label("请填写家庭成员信息:"),
                formGrid,
                buttonBox);
        mainLayout.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // 设置场景
        Scene scene = new Scene(mainLayout);
        dialog.setScene(scene);

        // 显示并等待关闭
        dialog.showAndWait();
    }

    /**
     * 生成Excel格式的示例模板（适用于POI库可用的情况）
     *
     * @param templateType 模板类型（student或fee）
     * @param file         保存的文件
     * @return 成功返回true，失败返回false
     */
    private boolean createExcelTemplate(String templateType, File file) {
        try {
            // 由于模块问题，我们使用CSV格式作为替代
            StringBuilder content = new StringBuilder();

            if (templateType.equals("student")) {
                // 学生数据模板
                content.append("学号,姓名,院系,专业,班级,性别,出生日期,证件号码,邮箱,电话,地址\n");
                content.append(
                        "2022030001,张三,软件学院,软件工程,软工1班,男,2001-01-01,370123200101010001,zhangsan@example.com,13800138000,山东省济南市\n");
                content.append(
                        "2022030002,李四,软件学院,软件工程,软工1班,女,2001-02-02,370123200102020002,lisi@example.com,13800138001,山东省青岛市\n");
            } else {
                // 消费记录模板
                content.append("日期,金额,消费类型,消费地点,备注\n");
                content.append("2023-01-01,10.50,餐饮,学生食堂,早餐\n");
                content.append("2023-01-01,15.00,餐饮,学生食堂,午餐\n");
                content.append("2023-01-01,12.50,餐饮,学生食堂,晚餐\n");
                content.append("2023-01-02,5.50,超市,校内超市,日用品\n");
            }

            // 写入文件
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(content.toString().getBytes());
            }

            MessageDialog.showDialog("已生成CSV格式模板。请注意：\n\n" +
                    "1. 请使用Excel打开此文件\n" +
                    "2. 在Excel中编辑后，另存为Excel格式(.xlsx)文件\n" +
                    "3. 使用保存的Excel文件进行导入");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
