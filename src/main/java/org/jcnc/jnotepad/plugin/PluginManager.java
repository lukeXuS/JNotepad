package org.jcnc.jnotepad.plugin;

import org.jcnc.jnotepad.common.manager.ThreadPoolManager;
import org.jcnc.jnotepad.controller.config.AppConfigController;
import org.jcnc.jnotepad.model.entity.PluginInfo;
import org.jcnc.jnotepad.util.LogUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.jcnc.jnotepad.plugin.PluginLoader.readPlugin;

/**
 * 插件管理器
 * <p>
 * 该类用于管理插件的加载和执行。
 * 插件可以通过加载外部JAR文件中的类来扩展应用程序的功能。
 *
 * @author luke
 */
public class PluginManager {
    private static final PluginManager INSTANCE = new PluginManager();
    Logger logger = LogUtil.getLogger(this.getClass());
    /**
     * 插件类别
     */
    private final Map<String, List<String>> categories = new HashMap<>();
    /**
     * 插件信息
     */
    private final List<PluginInfo> pluginInfos = new ArrayList<>();

    private PluginManager() {

    }

    public static PluginManager getInstance() {
        return INSTANCE;
    }


    /**
     * 卸载插件
     *
     * @param pluginInfo 插件信息类
     * @since 2023/9/11 12:28
     */
    public void unloadPlugin(PluginInfo pluginInfo) {
        // 删除集合中的插件信息
        pluginInfos.remove(pluginInfo);
        AppConfigController instance = AppConfigController.getInstance();
        instance.getAppConfig().getPlugins().remove(pluginInfo);
        // 刷新配置
        instance.writeAppConfig();
        // 删除本地插件jar包
        Path plungsPath = instance.getPlungsPath();
        try (Stream<Path> pathStream = Files.walk(plungsPath)) {
            pathStream.filter(path -> path.toString().endsWith(".jar")).forEach(path -> {
                try {
                    File pluginJar = new File(path.toString());
                    PluginInfo temp = readPlugin(pluginJar);
                    if ((temp.getName() + temp.getAuthor()).equals(pluginInfo.getName() + pluginInfo.getAuthor())) {
                        Files.delete(pluginJar.toPath());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        ThreadPoolManager.threadContSelfSubtracting();
    }

    /**
     * 禁用插件
     *
     * @param pluginInfo 需要禁用的某个插件的插件类
     * @apiNote
     * @since 2023/9/11 12:34
     */
    public void disablePlugIn(PluginInfo pluginInfo) {
        pluginInfo.setEnabled(false);
        pluginInfo.setPlugin(null);
        ThreadPoolManager.getThreadPool().submit(() -> {
            AppConfigController instance = AppConfigController.getInstance();
            instance.getAppConfig().getPlugins().forEach(plugin -> {
                if ((pluginInfo.getName() + pluginInfo.getAuthor()).equals(plugin.getName() + plugin.getAuthor())) {
                    plugin.setEnabled(false);
                }
            });
            instance.writeAppConfig();
            ThreadPoolManager.threadContSelfSubtracting();
        });
    }

    /**
     * 初始化所有启用的插件
     */
    public void initializePlugins() {
        for (PluginInfo pluginInfo : pluginInfos) {
            if (pluginInfo.isEnabled()) {
                pluginInfo.getPlugin().initialize();
            }
        }
    }

    /**
     * 执行插件
     *
     * @param pluginInfo 需要执行的插件的信息类
     * @apiNote
     * @since 2023/9/16 14:58
     */
    public void executePlugin(PluginInfo pluginInfo) {
        pluginInfo.getPlugin().execute();
    }

    /**
     * 执行加载的插件
     * todo 待移除
     */
    public void executePlugins() {
        for (PluginInfo pluginInfo : pluginInfos) {
            if (pluginInfo.isEnabled()) {
                pluginInfo.getPlugin().execute();
            }
        }
    }

    /**
     * 获取按类别分类的已加载插件
     *
     * @return 插件类别映射
     */
    public Map<String, List<String>> getLoadedPluginsByCategory() {
        return categories;
    }

    public List<PluginInfo> getPluginInfos() {
        return pluginInfos;
    }
}
