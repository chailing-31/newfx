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

    private Integer personId = null;//当前编辑修改的学生的主键
    private String num = null;

    private ArrayList<Map> studentList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据
    private ObservableList<Map> observableList = FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < studentList.size(); j++) {
            observableList.addAll(FXCollections.observableArrayList(studentList.get(j)));
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
        DataResponse res;
        DataRequest req = new DataRequest();
        req.add("numName", "");
        res = HttpRequestUtil.request("/api/student/getStudentList", req); //从后台获取所有学生信息列表集合
        if (res != null && res.getCode() == 0) {
            studentList = (ArrayList<Map>) res.getData();
        }

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

        setTableViewData();

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
    }

    protected void changeStudentInfo() {
        Map<String,Object> form = dataTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            clearPanel();
            return;
        }
        num = String.valueOf(CommonMethod.getInteger(form, "num"));
        DataRequest req = new DataRequest();
        req.add("num", num);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentInfo", req);
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

/*    /**
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
/*
    /**
     * 添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
    @FXML
    protected void onAddButtonClick() {
        clearPanel();
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
        if(res != null) {
            if (res.getCode() == 0) {
                MessageDialog.showDialog("删除成功！");
                String numName = numNameTextField.getText();
                DataRequest re = new DataRequest();
                re.add("numName", "");
                DataResponse response = HttpRequestUtil.request("/api/student/getStudentList", re);
                if (response != null && res.getCode() == 0) {
                    studentList = (ArrayList<Map>) response.getData();
                    setTableViewData();
                }
            } else {
                MessageDialog.showDialog(res.getMsg());
            }
       }
    }

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     * */
    @FXML
    protected void onSaveButtonClick() {
        if (numField.getText().isEmpty()) {
            MessageDialog.showDialog("学号为空，不能修改");
            return;
        }
        Map<String,Object> form = new HashMap<>();
        form.put("num", numField.getText());
        form.put("name", nameField.getText());
        form.put("dept", deptField.getText());
        form.put("major", majorField.getText());
        form.put("className", classNameField.getText());
        form.put("card", cardField.getText());
        if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
            form.put("gender", genderComboBox.getSelectionModel().getSelectedItem().getValue());
        form.put("birthday", birthdayPick.getEditor().getText());
        form.put("email", emailField.getText());
        form.put("phone", phoneField.getText());
        form.put("address", addressField.getText());

        DataRequest req = new DataRequest();
        req.add("personId", null);
        req.add("form", form);
        DataResponse res = HttpRequestUtil.request("/api/student/studentEditSave", req);
        if (res.getCode() == 0) {
            personId = CommonMethod.getIntegerFromObject(res.getData());
            MessageDialog.showDialog("提交成功！");
            String numName = numNameTextField.getText();
            DataRequest re = new DataRequest();
            re.add("numName", "");
            DataResponse response = HttpRequestUtil.request("/api/student/getStudentList", re);
            if (response != null && response.getCode() == 0) {
                studentList = (ArrayList<Map>) response.getData();
                setTableViewData();
            }        } else {
            MessageDialog.showDialog(res.getMsg());
        }

    }

    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择学生数据表");
        fileDialog.setInitialDirectory(new File("D:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                        File file = fileDialog.showOpenDialog(null);
        String paras = "";
        DataResponse res = HttpRequestUtil.importData("/api/term/importStudentData", file.getPath(), paras);
        if (res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        } else {
            MessageDialog.showDialog(res.getMsg());
            }
    }

    @FXML
    protected void onFamilyButtonClick() {
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
        table.getColumns().add(nameColumn);

        // 性别列
        TableColumn<Map, String> genderColumn = new TableColumn<>("性别");
        genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        genderColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        table.getColumns().add(genderColumn);

        // 年龄列
        TableColumn<Map, String> ageColumn = new TableColumn<>("年龄");
        ageColumn.setCellValueFactory(new MapValueFactory<>("age"));
        ageColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        table.getColumns().add(ageColumn);

        // 单位列
        TableColumn<Map, String> unitColumn = new TableColumn<>("单位");
        unitColumn.setCellValueFactory(new MapValueFactory<>("unit"));
        unitColumn.setCellFactory(TextFieldTableCell.<Map>forTableColumn());
        table.getColumns().add(unitColumn);

        // 创建框架布局
        BorderPane root = new BorderPane();
        FlowPane flowPane = new FlowPane();
        Button obButton = new Button("确定");
        obButton.setOnAction(event -> {
            for(Map map: table.getItems()) {
                System.out.println("map:"+map);
            }
            stage.close();
        });


        stage.initOwner(MainApplication.getMainStage());
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setTitle("成绩录入对话框！");
        stage.setOnCloseRequest(event -> {
            MainApplication.setCanClose(true);
        });

        stage.showAndWait();
    }

    /**
     * 保存所有家庭成员信息
     */
/*    private void saveFamilyMembers(Integer personId, ObservableList<Map> familyMembers) {
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
    }*/

    public void displayPhoto(){

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

        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("图片上传");
//        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
        File file = fileDialog.showOpenDialog(null);
        if(file == null)
            return;
        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhoto",file.getPath(),"photo/" + personId + ".jpg");
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            displayPhoto();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    public void onImportFeeButtonClick(){
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择消费数据表");
        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "personId="+personId;
        DataResponse res =HttpRequestUtil.importData("/api/student/importFeeData",file.getPath(),paras);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");}
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
