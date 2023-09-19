package org.jcnc.jnotepad;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jcnc.jnotepad.app.i18n.UiResourceBundle;
import org.jcnc.jnotepad.common.constants.AppConstants;
import org.jcnc.jnotepad.common.constants.TextConstants;
import org.jcnc.jnotepad.common.manager.ThreadPoolManager;
import org.jcnc.jnotepad.controller.ResourceController;
import org.jcnc.jnotepad.controller.config.PluginConfigController;
import org.jcnc.jnotepad.controller.i18n.LocalizationController;
import org.jcnc.jnotepad.controller.manager.Controller;
import org.jcnc.jnotepad.plugin.PluginManager;
import org.jcnc.jnotepad.util.UiUtil;
import org.jcnc.jnotepad.views.manager.ViewManager;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * 启动程序类
 *
 * <p>该类用于启动 JNotepad 记事本应用程序。</p>
 *
 * @author 许轲
 */
public class LunchApp extends Application {

    private static final Pane ROOT = new Pane();
    private static final Scene SCENE;

    static {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        double width = AppConstants.SCREEN_WIDTH;
        double length = AppConstants.SCREEN_LENGTH;
        SCENE = new Scene(ROOT, width, length);
    }

    /**
     * 线程池
     */
    private final ExecutorService threadPool = ThreadPoolManager.getThreadPool();

    /**
     * 应用程序的入口点，启动 JavaFX 应用程序。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 获取当前窗口。
     *
     * @return 当前窗口
     */
    public static Window getWindow() {
        return SCENE.getWindow();
    }

    @Override
    public void start(Stage primaryStage) {
        SCENE.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/styles.css")).toExternalForm());
        // 初始化UI组件
        initUiComponents();
        UiResourceBundle.bindStringProperty(primaryStage.titleProperty(), TextConstants.TITLE);

        primaryStage.setScene(SCENE);
        primaryStage.setWidth(SCENE.getWidth());
        primaryStage.setHeight(SCENE.getHeight());
        primaryStage.getIcons().add(UiUtil.getAppIcon());

        primaryStage.show();
    }

    private void initUiComponents() {

        // 1. 加载语言
        LocalizationController.initLocal();
        // 2. 加载资源
        ResourceController.getInstance().loadResources();
        // 3. 初始化插件
        PluginManager.getInstance().initializePlugins();
        // 3. 加载组件
        ViewManager viewManager = ViewManager.getInstance(SCENE);
        viewManager.initScreen(SCENE);

        // 使用线程池加载关联文件并创建文本区域
        List<String> rawParameters = getParameters().getRaw();
        Controller.getInstance().openAssociatedFileAndCreateTextArea(rawParameters);
    }

    @Override
    public void stop() {
        PluginConfigController instance = PluginConfigController.getInstance();
        // 刷新插件配置文件
        instance.getConfig().setPlugins(PluginManager.getInstance().getPluginInfos());
        instance.writeConfig();
        // 关闭线程池
        threadPool.shutdownNow();
    }
}
