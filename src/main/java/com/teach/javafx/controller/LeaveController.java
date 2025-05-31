package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeaveController 请假管理控制类 对应 leave-panel.fxml
 */
public class LeaveController extends ToolController {
    @FXML
    private TextField numNameTextField;
    @FXML
    private ComboBox<OptionItem> leaveTypeFilterComboBox;
    @FXML
    private ComboBox<OptionItem> statusFilterComboBox;
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> numColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> leaveTypeColumn;
    @FXML
    private TableColumn<Map, String> startDateColumn;
    @FXML
    private TableColumn<Map, String> endDateColumn;
    @FXML
    private TableColumn<Map, String> daysColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;

    // 编辑表单控件
    @FXML
    private TextField numField;
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    @FXML
    private ComboBox<OptionItem> leaveTypeComboBox;
    @FXML
    private ComboBox<OptionItem> statusComboBox;
    @FXML
    private ComboBox<OptionItem> teacherComboBox;
    private List<OptionItem> teacherList;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField daysField;
    @FXML
    private TextArea reasonField;
    @FXML
    private TextArea approveCommentField;

    private Integer leaveId = null;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    public List<OptionItem> getTeacherList() {
        return teacherList;
    }

    /**
     * 页面初始化
     */
    @FXML
    public void initialize() {
        // 初始化表格列
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        leaveTypeColumn.setCellValueFactory(new MapValueFactory<>("leaveType"));
        startDateColumn.setCellValueFactory(new MapValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new MapValueFactory<>("endDate"));
        daysColumn.setCellValueFactory(new MapValueFactory<>("days"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));

        dataTableView.setItems(observableList);

        // 监听表格选择变化
        dataTableView.getSelectionModel().getSelectedItems().addListener(
                (javafx.collections.ListChangeListener<Map>) change -> {
                    while (change.next()) {
                        if (change.wasAdded() && !change.getAddedSubList().isEmpty()) {
                            Map selectedItem = change.getAddedSubList().get(0);
                            if (selectedItem != null) {
                                leaveId = CommonMethod.getInteger(selectedItem, "leaveId");
                                showLeaveDetails();
                            }
                        }
                    }
                });

        // 初始化下拉框
        initializeComboBoxes();
        loadStudentList();
        onQueryButtonClick();
    }

    /**
     * 初始化下拉框
     */
    private void initializeComboBoxes() {
        // 请假类型下拉框
        List<OptionItem> leaveTypeList = new ArrayList<>();
        leaveTypeList.add(new OptionItem(1, "病假", "病假"));
        leaveTypeList.add(new OptionItem(2, "事假", "事假"));
        leaveTypeList.add(new OptionItem(3, "其他", "其他"));
        leaveTypeComboBox.setItems(FXCollections.observableArrayList(leaveTypeList));

        // 筛选用的请假类型（包含"全部"选项）
        List<OptionItem> filterLeaveTypeList = new ArrayList<>();
        filterLeaveTypeList.add(new OptionItem(0, "", "全部类型"));
        filterLeaveTypeList.addAll(leaveTypeList);
        leaveTypeFilterComboBox.setItems(FXCollections.observableArrayList(filterLeaveTypeList));

        // 状态下拉框
        List<OptionItem> statusList = new ArrayList<>();
        statusList.add(new OptionItem(1, "待审批", "待审批"));
        statusList.add(new OptionItem(2, "已批准", "已批准"));
        statusList.add(new OptionItem(3, "已拒绝", "已拒绝"));
        statusComboBox.setItems(FXCollections.observableArrayList(statusList));

        // 筛选用的状态（包含"全部"选项）
        List<OptionItem> filterStatusList = new ArrayList<>();
        filterStatusList.add(new OptionItem(0, "", "全部状态"));
        filterStatusList.addAll(statusList);
        statusFilterComboBox.setItems(FXCollections.observableArrayList(filterStatusList));

        //初始化老师下拉框
        DataRequest req =new DataRequest();
        teacherList = HttpRequestUtil.requestOptionItemList("/api/teacher/getTeacherList",req); //从后台获取所有学生信息列表集合
        OptionItem item = new OptionItem(null,"0","请选择");
        teacherComboBox.getItems().addAll(item);
        teacherComboBox.getItems().addAll(teacherList);

    }

    /**
     * 加载学生列表
     */
    private void loadStudentList() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/leave/getStudentList", req);
        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> studentList = (List<Map<String, Object>>) res.getData();
            List<OptionItem> options = new ArrayList<>();

            for (Map<String, Object> student : studentList) {
                Integer studentId = CommonMethod.getInteger(student, "value");
                String name = CommonMethod.getString(student, "title");
                options.add(new OptionItem(studentId, studentId.toString(), name));
            }

            studentComboBox.setItems(FXCollections.observableArrayList(options));
        }
    }

    /**
     * 查询按钮点击事件
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        String leaveType = "";
        String status = "";

        if (leaveTypeFilterComboBox.getSelectionModel().getSelectedItem() != null) {
            leaveType = leaveTypeFilterComboBox.getSelectionModel().getSelectedItem().getValue();
        }
        if (statusFilterComboBox.getSelectionModel().getSelectedItem() != null) {
            status = statusFilterComboBox.getSelectionModel().getSelectedItem().getValue();
        }

        DataRequest req = new DataRequest();
        req.add("numName", numName);
        req.add("leaveType", leaveType);
        req.add("status", status);

        DataResponse res = HttpRequestUtil.request("/api/leave/getLeaveList", req);
        if (res != null && res.getCode() == 0) {
            observableList.clear();
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) res.getData();
            if (dataList != null && !dataList.isEmpty()) {
                observableList.addAll(dataList);
            }
        } else {
            MessageDialog.showDialog("查询失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    /**
     * 新建按钮点击事件
     */
    @FXML
    protected void onAddButtonClick() {
        leaveId = null;
        clearFormFields();
        // 设置默认编号
        generateDefaultNum();
    }

    /**
     * 保存按钮点击事件
     */
    @FXML
    protected void onSaveButtonClick() {
        String num = numField.getText().trim();

        if (num.isEmpty()) {
            MessageDialog.showDialog("请假编号不能为空！");
            return;
        }

        if (studentComboBox.getSelectionModel().getSelectedItem() == null) {
            MessageDialog.showDialog("请选择学生！");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("num", num);
        form.put("studentId", Integer.parseInt(studentComboBox.getSelectionModel().getSelectedItem().getValue()));
        form.put("leaveType",
                leaveTypeComboBox.getSelectionModel().getSelectedItem() != null
                        ? leaveTypeComboBox.getSelectionModel().getSelectedItem().getValue()
                        : "事假");
        form.put("startDate",
                startDatePicker.getValue() != null ? startDatePicker.getValue().toString() + " 08:00:00" : "");
        form.put("endDate", endDatePicker.getValue() != null ? endDatePicker.getValue().toString() + " 18:00:00" : "");
        form.put("reason", reasonField.getText().trim());
        form.put("status",
                statusComboBox.getSelectionModel().getSelectedItem() != null
                        ? statusComboBox.getSelectionModel().getSelectedItem().getValue()
                        : "待审批");

        String daysText = daysField.getText().trim();
        if (!daysText.isEmpty()) {
            try {
                form.put("days", Integer.parseInt(daysText));
            } catch (NumberFormatException e) {
                MessageDialog.showDialog("请假天数必须是数字！");
                return;
            }
        }

        form.put("approveComment", approveCommentField.getText().trim());

        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);
        req.add("form", form);

        DataResponse res = HttpRequestUtil.request("/api/leave/leaveEditSave", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            onQueryButtonClick();
            if (leaveId == null) {
                clearFormFields();
                generateDefaultNum();
            }
        } else {
            MessageDialog.showDialog("保存失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    /**
     * 删除按钮点击事件
     */
    @FXML
    protected void onDeleteButtonClick() {
        if (leaveId == null) {
            MessageDialog.showDialog("请先选择要删除的请假记录！");
            return;
        }

        int choice = MessageDialog.choiceDialog("确定要删除所选请假记录吗？");
        if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);

        DataResponse res = HttpRequestUtil.request("/api/leave/leaveDelete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            leaveId = null;
            clearFormFields();
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    /**
     * 批准按钮点击事件
     */
    @FXML
    protected void onApproveButtonClick() {
        if (leaveId == null) {
            MessageDialog.showDialog("请先选择要批准的请假记录！");
            return;
        }

        int choice = MessageDialog.choiceDialog("确定要批准这个请假申请吗？");
        if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);
        req.add("status", "已批准");
        req.add("approveComment", approveCommentField.getText().trim());

        DataResponse res = HttpRequestUtil.request("/api/leave/approveLeave", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("批准成功！");
            onQueryButtonClick();
            showLeaveDetails();
        } else {
            MessageDialog.showDialog("批准失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    /**
     * 拒绝按钮点击事件
     */
    @FXML
    protected void onRejectButtonClick() {
        if (leaveId == null) {
            MessageDialog.showDialog("请先选择要拒绝的请假记录！");
            return;
        }

        int choice = MessageDialog.choiceDialog("确定要拒绝这个请假申请吗？");
        if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);
        req.add("status", "已拒绝");
        req.add("approveComment", approveCommentField.getText().trim());

        DataResponse res = HttpRequestUtil.request("/api/leave/approveLeave", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("拒绝成功！");
            onQueryButtonClick();
            showLeaveDetails();
        } else {
            MessageDialog.showDialog("拒绝失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    /**
     * 导出Excel按钮点击事件
     */
    @FXML
    protected void onExportButtonClick() {
        String numName = numNameTextField.getText();

        DataRequest req = new DataRequest();
        req.add("numName", numName);

        byte[] bytes = HttpRequestUtil.requestByteData("/api/leave/getLeaveListExcel", req);
        if (bytes != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存Excel文件");
                fileChooser.setInitialFileName("请假列表.xlsx");
                File file = fileChooser.showSaveDialog(dataTableView.getScene().getWindow());
                if (file != null) {
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(bytes);
                        MessageDialog.showDialog("导出成功！");
                    }
                }
            } catch (Exception e) {
                MessageDialog.showDialog("导出失败: " + e.getMessage());
            }
        } else {
            MessageDialog.showDialog("导出失败！");
        }
    }

    /**
     * 显示请假详细信息
     */
    private void showLeaveDetails() {
        if (leaveId == null)
            return;

        DataRequest req = new DataRequest();
        req.add("leaveId", leaveId);

        DataResponse res = HttpRequestUtil.request("/api/leave/getLeaveInfo", req);
        if (res != null && res.getCode() == 0) {
            Map<String, Object> data = (Map<String, Object>) res.getData();
            if (data != null) {
                fillFormFields(data);
            }
        }
    }

    /**
     * 填充表单字段
     */
    private void fillFormFields(Map<String, Object> data) {
        numField.setText(CommonMethod.getString(data, "num"));

        // 设置学生
        Integer studentId = CommonMethod.getInteger(data, "studentId");
        if (studentId != null) {
            studentComboBox.getItems().forEach(item -> {
                if (item.getValue().equals(studentId.toString())) {
                    studentComboBox.getSelectionModel().select(item);
                }
            });
        }

        // 设置请假类型
        String leaveType = CommonMethod.getString(data, "leaveType");
        leaveTypeComboBox.getItems().forEach(item -> {
            if (item.getValue().equals(leaveType)) {
                leaveTypeComboBox.getSelectionModel().select(item);
            }
        });

        // 设置时间字段
        String startDate = CommonMethod.getString(data, "startDate");
        if (!startDate.isEmpty()) {
            try {
                startDatePicker.setValue(LocalDate.parse(startDate.substring(0, 10)));
            } catch (Exception ignored) {
            }
        }

        String endDate = CommonMethod.getString(data, "endDate");
        if (!endDate.isEmpty()) {
            try {
                endDatePicker.setValue(LocalDate.parse(endDate.substring(0, 10)));
            } catch (Exception ignored) {
            }
        }

        daysField.setText(CommonMethod.getString(data, "days"));
        reasonField.setText(CommonMethod.getString(data, "reason"));
        approveCommentField.setText(CommonMethod.getString(data, "approveComment"));

        // 设置状态
        String status = CommonMethod.getString(data, "status");
        statusComboBox.getItems().forEach(item -> {
            if (item.getValue().equals(status)) {
                statusComboBox.getSelectionModel().select(item);
            }
        });
    }

    /**
     * 清空表单字段
     */
    private void clearFormFields() {
        numField.clear();
        studentComboBox.getSelectionModel().clearSelection();
        leaveTypeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        daysField.clear();
        reasonField.clear();
        approveCommentField.clear();
    }

    /**
     * 生成默认编号
     */
    private void generateDefaultNum() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String defaultNum = "LEAVE" + date + String.format("%03d", (int) (Math.random() * 1000));
        numField.setText(defaultNum);
    }
}