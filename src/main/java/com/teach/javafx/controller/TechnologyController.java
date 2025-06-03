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
 * TechnologyController 竞赛管理控制类 对应 technology-panel.fxml
 * 包含竞赛信息的增删改查功能
 */
public class TechnologyController extends ToolController{
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

    // 编辑表单控件
    @FXML
    private TextField numField;
    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<OptionItem> typeComboBox;
    @FXML
    private TextArea descriptionField;

    private Integer technologyId = null;
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
//        startTimeColumn.setCellValueFactory(new MapValueFactory<>("startTime"));
//        statusColumn.setCellValueFactory(new MapValueFactory<>("status"));
//        organizerColumn.setCellValueFactory(new MapValueFactory<>("organizer"));

        dataTableView.setItems(observableList);

        // 监听表格选择变化
        dataTableView.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<Map>) change -> {
                    while (change.next()) {
                        if (change.wasAdded()) {
                            Map selectedItem = change.getAddedSubList().get(0);
                            if (selectedItem != null) {
                                technologyId = CommonMethod.getInteger(selectedItem, "technologyId");
                                showTechnologyDetails();
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
        // 科技成果类型下拉框
        List<OptionItem> typeList = new ArrayList<>();
        typeList.add(new OptionItem(1, "工业科技", "工业科技"));
        typeList.add(new OptionItem(2, "医疗技术", "医疗技术"));
        typeList.add(new OptionItem(3, "环境科技", "环境科技"));
        typeList.add(new OptionItem(4, "农业科技", "农业科技"));
        typeComboBox.setItems(FXCollections.observableArrayList(typeList));

    }

    /**
     * 查询按钮点击事件
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();

        DataRequest req = new DataRequest();
        req.add("numName", numName);

        DataResponse res = HttpRequestUtil.request("/api/technology/getTechnologyList", req);
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
        technologyId = null;
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
            MessageDialog.showDialog("科技成果编号不能为空！");
            return;
        }
        if (name.isEmpty()) {
            MessageDialog.showDialog("科技成果名称不能为空！");
            return;
        }

        Map<String, Object> form = new HashMap<>();
        form.put("num", num);
        form.put("name", name);
        form.put("type",
                typeComboBox.getSelectionModel().getSelectedItem() != null
                        ? typeComboBox.getSelectionModel().getSelectedItem().getValue()
                        : "");

        form.put("description", descriptionField.getText().trim());

        DataRequest req = new DataRequest();
        req.add("technologyId", technologyId);
        req.add("form", form);

        DataResponse res = HttpRequestUtil.request("/api/technology/technologyEditSave", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("保存成功！");
            onQueryButtonClick();
            if (technologyId == null) {
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
        if (technologyId == null) {
            MessageDialog.showDialog("请先选择要删除的科技成果！");
            return;
        }

        int choice = MessageDialog.choiceDialog("确定要删除所选科技成果吗？");
        if (choice != MessageDialog.CHOICE_YES) {
            return;
        }

        DataRequest req = new DataRequest();
        req.add("technologyId", technologyId);

        DataResponse res = HttpRequestUtil.request("/api/technology/technologyDelete", req);
        if (res != null && res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            technologyId = null;
            clearFormFields();
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog("删除失败: " + (res != null ? res.getMsg() : "网络错误"));
        }
    }

    /**
     * 导出Excel按钮点击事件
     */
/*    @FXML
    protected void onExportButtonClick() {
        String numName = numNameTextField.getText();

        DataRequest req = new DataRequest();
        req.add("numName", numName);

        byte[] bytes = HttpRequestUtil.requestByteData("/api/technology/getTechnologyListExcel", req);
        if (bytes != null) {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("保存Excel文件");
                fileChooser.setInitialFileName("科技成果列表.xlsx");
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
*/
    /**
     * 显示竞赛详细信息
     */
    private void showTechnologyDetails() {
        if (technologyId == null)
            return;

        DataRequest req = new DataRequest();
        req.add("technologyId", technologyId);

        DataResponse res = HttpRequestUtil.request("/api/technology/getTechnologyInfo", req);
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
        descriptionField.setText(CommonMethod.getString(data, "description"));
    }

    /**
     * 清空表单字段
     */
    private void clearFormFields() {
        numField.clear();
        nameField.clear();
        typeComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
    }
}
