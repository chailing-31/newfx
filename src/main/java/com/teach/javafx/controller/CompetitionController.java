package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CompetitionController 竞赛管理控制类 对应 competition-panel.fxml
 * 包含竞赛信息的增删改查功能
 */
public class CompetitionController extends ToolController {
    @FXML
    private TextField numNameTextField;
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> numColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;
    @FXML
    private TableColumn<Map, String> typeColumn;
    @FXML
    private TableColumn<Map, String> startTimeColumn;
    @FXML
    private TableColumn<Map, String> statusColumn;
    @FXML
    private TableColumn<Map, String> organizerColumn;

    // 编辑表单控件
    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<OptionItem> typeComboBox;
    @FXML
    private DatePicker startTimePicker;
    @FXML
    private DatePicker endTimePicker;
    @FXML
    private DatePicker registrationDeadlinePicker;
    @FXML
    private TextField locationField;
    @FXML
    private TextField organizerField;
    @FXML
    private TextArea awardsField;
    @FXML
    private TextArea requirementsField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<OptionItem> statusComboBox;
    @FXML
    private TextField maxParticipantsField;

    private Integer competitionId = null;
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    /**
     * 页面初始化
     */
    @FXML
    public void initialize() {
        // 初始化表格列
        numColumn.setCellValueFactory(new MapValueFactory<>("num"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("type"));
        startTimeColumn.setCellValueFactory(new MapValueFactory<>("startTime"));
        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
        organizerColumn.setCellValueFactory(new MapValueFactory<>("organizer"));

        dataTableView.setItems(observableList);

        // 监听表格选择变化
        dataTableView.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<Map>) change -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            Map selectedItem = change.getAddedSubList().get(0);
                            if (selectedItem != null) {
                                competitionId = CommonMethod.getInteger(selectedItem, "competitionId");
                                showCompetitionDetails();
                            }
                        }
                    }
                });

        initializeComboBoxes();
        onQueryButtonClick();
    }

    /**
     * 初始化下拉框
     */
    private void initializeComboBoxes() {
        // 竞赛类型下拉框
        List<OptionItem> typeList = new ArrayList<>();
        typeList.add(new OptionItem(1, "编程竞赛", "编程竞赛"));
        typeList.add(new OptionItem(2, "数学竞赛", "数学竞赛"));
        typeList.add(new OptionItem(3, "创新竞赛", "创新竞赛"));
        typeList.add(new OptionItem(4,"其他","其他"));
        typeComboBox.setItems(FXCollections.observableArrayList(typeList));

        // 竞赛状态下拉框
        List<OptionItem> statusList = new ArrayList<>();
        statusList.add(new OptionItem(1, "未开始", "未开始"));
        statusList.add(new OptionItem(2, "进行中", "进行中"));
        statusList.add(new OptionItem(3, "已结束", "已结束"));
        statusComboBox.setItems(FXCollections.observableArrayList(statusList));
    }

    /**
     * 查询按钮点击事件
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();

        DataRequest req = new DataRequest();
        req.add("numName", numName);

        DataResponse res = HttpRequestUtil.request("/api/competition/getCompetitionList", req);
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
        competitionId = null;
        clearFormFields();
        // 设置默认值
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 保存按钮点击事件
     */
    @FXML
    protected void onSaveButtonClick() {
        String num = numField.getText().trim();
        String name = nameField.getText().trim();

        if (num.isEmpty()) {
            MessageDialog.showDialog("竞赛编号不能为空！");
            return;
        }
        if (name.isEmpty()) {
            MessageDialog.showDialog("竞赛名称不能为空！");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("num", num);
        form.put("name", name);
        form.put("type",
                typeComboBox.getSelectionModel().getSelectedItem() != null
                        ? typeComboBox.getSelectionModel().getSelectedItem().getValue()
                        : "");
        form.put("startTime",
                startTimePicker.getValue() != null ? startTimePicker.getValue().toString() + " 00:00:00" : "");
        form.put("endTime", endTimePicker.getValue() != null ? endTimePicker.getValue().toString() + " 23:59:59" : "");
        form.put("registrationDeadline",
                registrationDeadlinePicker.getValue() != null
                        ? registrationDeadlinePicker.getValue().toString() + " 23:59:59"
                        : "");
        form.put("location", locationField.getText().trim());
        form.put("organizer", organizerField.getText().trim());
        form.put("awards", awardsField.getText().trim());
        form.put("requirements", requirementsField.getText().trim());
        form.put("description", descriptionField.getText().trim());
        form.put("status",
                statusComboBox.getSelectionModel().getSelectedItem() != null
                        ? statusComboBox.getSelectionModel().getSelectedItem().getValue()
                        : "未开始");

        String maxParticipantsText = maxParticipantsField.getText().trim();
        if (!maxParticipantsText.isEmpty()) {
            try {
                form.put("maxParticipants", Integer.parseInt(maxParticipantsText));
            } catch (NumberFormatException e) {
                MessageDialog.showDialog("最大参赛人数必须是数字！");
                return;
            }
        }

        DataRequest req = new DataRequest();
        req.add("competitionId", competitionId);
        req.add("form", form);

        DataResponse res = HttpRequestUtil.request("/api/competition/competitionEditSave", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            onQueryButtonClick();
            if (competitionId == null) {
                clearFormFields();
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
        if (competitionId == null) {
            MessageDialog.showDialog("请先选择要删除的竞赛！");
            return;
        }

        int choice = MessageDialog.choiceDialog("确定要删除所选竞赛吗？");
        if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        req.add("competitionId", competitionId);

        DataResponse res = HttpRequestUtil.request("/api/competition/competitionDelete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            competitionId = null;
            clearFormFields();
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "网络错误"));
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

        byte[] bytes = HttpRequestUtil.requestByteData("/api/competition/getCompetitionListExcel", req);
        if (bytes != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存Excel文件");
                fileChooser.setInitialFileName("竞赛列表.xlsx");
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
     * 显示竞赛详细信息
     */
    private void showCompetitionDetails() {
        if (competitionId == null)
            return;

        DataRequest req = new DataRequest();
        req.add("competitionId", competitionId);

        DataResponse res = HttpRequestUtil.request("/api/competition/getCompetitionInfo", req);
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
        nameField.setText(CommonMethod.getString(data, "name"));

        // 设置竞赛类型
        String type = CommonMethod.getString(data, "type");
        typeComboBox.getItems().forEach(item -> {
            if (item.getValue().equals(type)) {
                typeComboBox.getSelectionModel().select(item);
            }
        });

        // 设置时间字段
        String startTime = CommonMethod.getString(data, "startTime");
        if (!startTime.isEmpty()) {
            try {
                startTimePicker.setValue(java.time.LocalDate.parse(startTime.substring(0, 10)));
            } catch (Exception ignored) {
            }
        }

        String endTime = CommonMethod.getString(data, "endTime");
        if (!endTime.isEmpty()) {
            try {
                endTimePicker.setValue(java.time.LocalDate.parse(endTime.substring(0, 10)));
            } catch (Exception ignored) {
            }
        }

        String registrationDeadline = CommonMethod.getString(data, "registrationDeadline");
        if (!registrationDeadline.isEmpty()) {
            try {
                registrationDeadlinePicker.setValue(java.time.LocalDate.parse(registrationDeadline.substring(0, 10)));
            } catch (Exception ignored) {
            }
        }

        locationField.setText(CommonMethod.getString(data, "location"));
        organizerField.setText(CommonMethod.getString(data, "organizer"));
        awardsField.setText(CommonMethod.getString(data, "awards"));
        requirementsField.setText(CommonMethod.getString(data, "requirements"));
        descriptionField.setText(CommonMethod.getString(data, "description"));
        maxParticipantsField.setText(CommonMethod.getString(data, "maxParticipants"));

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
        nameField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        startTimePicker.setValue(null);
        endTimePicker.setValue(null);
        registrationDeadlinePicker.setValue(null);
        locationField.clear();
        organizerField.clear();
        awardsField.clear();
        requirementsField.clear();
        descriptionField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        maxParticipantsField.clear();
    }
}