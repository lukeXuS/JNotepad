package org.jcnc.jnotepad.component.module.vbox;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jcnc.jnotepad.component.module.vbox.components.DebugBox;
import org.jcnc.jnotepad.component.module.vbox.components.RunBox;

/**
 * 底部Run,Debug,Cmd面板
 *
 * @author cccqyu
 */
public class BuildPanel extends TabPane {

    private static BuildPanel instance = null;

    public static BuildPanel getInstance() {

        if (instance == null) {
            instance = new BuildPanel();
        }
        return instance;
    }

    private final RunBox runBox;
    private final DebugBox debugBox;

    private BuildPanel() {
        TerminalConfig config = new TerminalConfig();
        TerminalBuilder builder = new TerminalBuilder(config);
        TerminalTab terminalTab = builder.newTerminal();
        terminalTab.setClosable(false);

        runBox = new RunBox();
        debugBox = new DebugBox();

        Tab runTab = new Tab("运行", runBox);
        runTab.setClosable(false);

        Tab buildTab = new Tab("构建", debugBox);
        buildTab.setClosable(false);

        this.getTabs().addAll(runTab, buildTab, terminalTab);
    }

    public RunBox getRunBox() {
        return runBox;
    }

    public DebugBox getBuildBox() {
        return debugBox;
    }
}
