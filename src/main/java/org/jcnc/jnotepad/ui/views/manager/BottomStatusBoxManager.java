package org.jcnc.jnotepad.ui.views.manager;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jcnc.jnotepad.api.core.views.manager.builder.BottomStatusBoxButtonBuilder;
import org.jcnc.jnotepad.app.common.constants.TextConstants;
import org.jcnc.jnotepad.app.i18n.UiResourceBundle;
import org.jcnc.jnotepad.ui.component.module.TextCodeArea;
import org.jcnc.jnotepad.ui.views.root.bottom.status.BottomStatusBox;
import org.jcnc.jnotepad.ui.views.root.center.main.center.tab.CenterTab;
import org.jcnc.jnotepad.ui.views.root.center.main.center.tab.CenterTabPane;
import org.kordamp.ikonli.javafx.FontIcon;

import java.nio.charset.Charset;

import static org.kordamp.ikonli.antdesignicons.AntDesignIconsFilled.UNLOCK;

/**
 * 状态栏组件管理类
 *
 * @author gewuyou
 */
public class BottomStatusBoxManager {
    private static final BottomStatusBoxManager INSTANCE = new BottomStatusBoxManager();

    private static final BottomStatusBox BOTTOM_STATUS_BOX = BottomStatusBox.getInstance();

    private static final String STATUS_LABEL_FORMAT = "%s : %d \t%s: %d \t%s: %d \t";

    private String style = "-fx-background-color: rgba(43,43,43,0.12);";

    public static BottomStatusBoxManager getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化状态栏
     */
    public void initStatusBox() {
        BOTTOM_STATUS_BOX.setStyle(style);
        BOTTOM_STATUS_BOX.getChildren().clear();
        registerBottomStatusBox();
        updateEncodingLabel();
        updateWhenTabSelected();
        BOTTOM_STATUS_BOX.getProperties().put("borderpane-margin", new Insets(5, 10, 5, 10));
        BOTTOM_STATUS_BOX.setAlignment(Pos.BASELINE_RIGHT);
        UiResourceBundle.getInstance().addListener((observable, oldValue, newValue) -> updateWhenTabSelected());

        /*
          第一个参数 10 表示上边距。
          第二个参数 10 表示右边距。
          第三个参数 10 表示下边距。
          第四个参数 10 表示左边距。
         */
        HBox.setMargin(BOTTOM_STATUS_BOX.getStatusLabel(), new Insets(5, 10, 5, 10));
    }

    /**
     * 注册下方状态栏
     */
    public void registerBottomStatusBox() {
        Label statusLabel = BOTTOM_STATUS_BOX.getStatusLabel();
        registerChildren(statusLabel);
        statusLabel.setText(getStatusBarFormattedText(0, 0, 1));
        registerChildren(BOTTOM_STATUS_BOX.getEncodingLabel());

        Button readOnlyButton = BOTTOM_STATUS_BOX.getReadOnlyButton();
        BottomStatusBoxButtonBuilder builder = new BottomStatusBoxButtonBuilder(readOnlyButton);
        FontIcon icon = FontIcon.of(UNLOCK);
        registerChildren(builder
                .setFontIcon(icon)
                .setEventHandler(e -> CenterTabPaneManager.getInstance().updateReadOnlyProperty(CenterTabPaneManager.getInstance().getSelected()))
                .build());
    }

    /**
     * 设置状态栏样式
     *
     * @param style 样式字符串
     */
    public void setBottomStatusBoxStyle(String style) {
        this.style = style;
    }


    /**
     * Registers the given node as a child of the current node.
     *
     * @param node the node to be registered as a child
     */
    public void registerChildren(Node node) {
        BOTTOM_STATUS_BOX.getChildren().add(node);
    }

    public void updateEncodingLabel() {
        updateEncodingLabel(null);
    }

    /**
     * 更新编码展示
     *
     * @param encoding 文件编码
     */
    public void updateEncodingLabel(String encoding) {
        if (encoding == null) {
            encoding = Charset.defaultCharset().name();
        }
        BOTTOM_STATUS_BOX.getEncodingLabel().setText(getEncodingFormattedText(encoding) + "\t");
    }

    /**
     * 更新字数统计
     */
    public void updateWordCountStatusLabel() {
        CenterTabPaneManager instance = CenterTabPaneManager.getInstance();
        if (instance.getSelected() == null) {
            return;
        }
        TextCodeArea textArea = instance.getSelected().getTextCodeArea();
        int caretPosition = textArea.getCaretPosition();
        int row = getRow(caretPosition, textArea.getText());
        int column = getColumn(caretPosition, textArea.getText());
        int length = textArea.getLength();
        BOTTOM_STATUS_BOX.getStatusLabel().setText(getStatusBarFormattedText(row, column, length));
    }

    /**
     * Tab选中时，更新状态栏
     * <br>1. 状态栏更新当前选中tab的数字统计
     * <br>2. 状态栏更新当前选中tab的字符编码
     */
    public void updateWhenTabSelected() {
        CenterTabPaneManager instance = CenterTabPaneManager.getInstance();
        if (instance.getSelected() != null) {
            updateWordCountStatusLabel();
            CenterTab centerTab = instance.getSelected();
            if (centerTab != null) {
                updateEncodingLabel(centerTab.getCharset().name());
                updateReadOnlyProperty(centerTab, CenterTabPane.getInstance().getTabs());
                // 添加光标位置变化监听器
                TextCodeArea textArea = centerTab.getTextCodeArea();
                textArea.caretPositionProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> updateRowColumnLabel(textArea.getCaretPosition(), textArea.getText()));
            }
        }
    }

    /**
     * 更新行列信息
     *
     * @param caretPosition 光标位置
     * @param text          文本内容
     */
    public void updateRowColumnLabel(int caretPosition, String text) {
        int row = getRow(caretPosition, text);
        int column = getColumn(caretPosition, text);
        BOTTOM_STATUS_BOX.getStatusLabel().setText(getStatusBarFormattedText(row, column, text.length()));
    }


    /**
     * 获取光标所在行号。
     *
     * @param caretPosition 光标位置
     * @param text          文本内容
     * @return 光标所在行号
     */
    public int getRow(int caretPosition, String text) {
        caretPosition = Math.min(caretPosition, text.length());
        String substring = text.substring(0, caretPosition);
        int count = 0;
        for (char c : substring.toCharArray()) {
            if (c == '\n') {
                count++;
            }
        }
        return count + 1;
    }

    /**
     * 获取光标所在列号。
     *
     * @param caretPosition 光标位置
     * @param text          文本内容
     * @return 光标所在列号
     */
    public int getColumn(int caretPosition, String text) {
        return caretPosition - text.lastIndexOf("\n", caretPosition - 1);
    }

    public String getStatusBarFormattedText(int row, int column, int wordCount) {
        String rowText = UiResourceBundle.getContent(TextConstants.ROW);
        String columnText = UiResourceBundle.getContent(TextConstants.COLUMN);
        String wordCountText = UiResourceBundle.getContent(TextConstants.WORD_COUNT);
        return String.format(STATUS_LABEL_FORMAT,
                rowText,
                row,
                columnText,
                column,
                wordCountText,
                wordCount
        );
    }

    public String getEncodingFormattedText(String encoding) {
        String encodingLabelFormat = "%s : %s";
        return String.format(encodingLabelFormat, UiResourceBundle.getContent(TextConstants.ENCODE), encoding);
    }

    /**
     * Updates the read-only property of a tab in the center tab pane.
     *
     * @param tab  the tab to update the read-only property for
     * @param tabs the list of tabs in the center tab pane
     */




    public void updateReadOnlyProperty(CenterTab tab, ObservableList<Tab> tabs) {
        Button readOnlyButton = BOTTOM_STATUS_BOX.getReadOnlyButton();
        readOnlyButton.setStyle("-fx-background-color: rgb(230, 230, 230);");
        readOnlyButton.setVisible(!tabs.isEmpty());

        ImageView icon;
        double iconSize = 20; // 修改圖標大小
        if (tab.getTextCodeArea().isEditable()) {
            icon = new ImageView(new Image("file:src/main/resources/jcnc/app/images/unlock.png"));
        } else {
            icon = new ImageView(new Image("file:src/main/resources/jcnc/app/images/lock.png"));
        }
        icon.setFitWidth(iconSize); // 設置圖標寬度
        icon.setFitHeight(iconSize); // 設置圖標高度
        readOnlyButton.setGraphic(icon);

        tab.getReadOnly().setSelected(!tab.getTextCodeArea().isEditable());
    }

}
