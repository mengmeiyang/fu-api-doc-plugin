package com.wdf.fudoc.components;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ClipboardSynchronizer;
import com.intellij.ide.IdeBundle;
import com.intellij.notification.EventLog;
import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.ClickListener;
import com.intellij.util.Alarm;
import com.intellij.util.text.DateFormatUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author wangdingfu
 * @date 2022-09-30 17:08:39
 */
public class StatusPanel extends JPanel {
    private Notification myCurrentNotification;
    private @NlsSafe @Nullable String myTimeText;
    private boolean myDirty;
    private boolean myAfterClick;
    private Alarm myLogAlarm;
    private Action myCopyAction;
    private Action myClearAction;
    private final TextPanel myTextPanel = new TextPanel() {
        @Override
        protected String getTextForPreferredSize() {
            return getText();
        }

        @Override
        public void setBounds(int x, int y, int w, int h) {
            super.setBounds(x, y, Math.min(w, StatusPanel.this.getWidth()), h);
        }

        @Override
        protected String truncateText(String text, Rectangle bounds, FontMetrics fm, Rectangle textR, Rectangle iconR, int maxWidth) {
            if (myTimeText != null && text.endsWith(myTimeText)) {
                int withoutTime = maxWidth - fm.stringWidth(myTimeText);
                Rectangle boundsForTrim = new Rectangle(withoutTime, bounds.height);
                return super.truncateText(text, boundsForTrim, fm, textR, iconR, withoutTime) + myTimeText;
            } else {
                return super.truncateText(text, bounds, fm, textR, iconR, maxWidth);
            }
        }
    };

    StatusPanel() {
        super(new BorderLayout());

        setOpaque(false);

        myTextPanel.setBorder(JBUI.Borders.emptyLeft(5));
        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent e, int clickCount) {
                if (myCurrentNotification != null || myAfterClick) {
                    EventLog.toggleLog(getActiveProject(), myCurrentNotification);
                    myAfterClick = true;
                    myTextPanel.setExplicitSize(myTextPanel.getSize());
                    UIUtil.setCursor(myTextPanel, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                return true;
            }
        }.installOn(myTextPanel);

        myTextPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                myTextPanel.setExplicitSize(null);
                myTextPanel.revalidate();
                myAfterClick = false;
                if (myCurrentNotification == null) {
                    UIUtil.setCursor(myTextPanel, Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (myCopyAction == null) myCopyAction = createCopyAction();

                    JBPopupMenu menu = new JBPopupMenu();
                    menu.add(new JBMenuItem(myCopyAction));

                    if (myClearAction == null) {
                        myClearAction = createClearAction();
                    }
                    if (myClearAction != null) {
                        menu.add(new JBMenuItem(myClearAction));
                    }
                    JBPopupMenu.showByEvent(e, menu);
                }
            }
        });

        add(myTextPanel, BorderLayout.WEST);
    }

    private Action createCopyAction() {
        ActionManager actionManager = ActionManager.getInstance();
        if (actionManager == null) return null;
        AnAction action = actionManager.getAction(IdeActions.ACTION_COPY);
        if (action == null) return null;
        return new AbstractAction(action.getTemplatePresentation().getText(), action.getTemplatePresentation().getIcon()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection content = new StringSelection(getText());
                ClipboardSynchronizer.getInstance().setContent(content, content);
            }

            @Override
            public boolean isEnabled() {
                return !getText().isEmpty();
            }
        };
    }

    private Action createClearAction() {
        Project project = getActiveProject();
        if (project == null) {
            return null;
        }
        return new AbstractAction(IdeBundle.message("clear.event.log.action", IdeBundle.message("toolwindow.stripe.Event_Log")),
                AllIcons.Actions.GC) {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventLog.doClear(project);
            }

            @Override
            public boolean isEnabled() {
                return EventLog.isClearAvailable(project);
            }
        };
    }

    @Nullable
    private Project getActiveProject() {
        // a better way of finding a project would be great
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            IdeFrame ideFrame = WindowManager.getInstance().getIdeFrame(project);
            if (ideFrame != null) {
                final JComponent frame = ideFrame.getComponent();
                if (SwingUtilities.isDescendingFrom(myTextPanel, frame)) {
                    return project;
                }
            }
        }
        return null;
    }

    // Returns the alarm used for displaying status messages in the status bar, or null if the status bar is attached to a floating
    // editor window.
    @Nullable
    private Alarm getAlarm() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        if (myLogAlarm == null || myLogAlarm.isDisposed()) {
            myLogAlarm = null; //Welcome screen
            Project project = getActiveProject();
            if (project != null && !project.isDisposed()) {
                myLogAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, project);
            }
        }
        return myLogAlarm;
    }

    public boolean updateText(@Nullable @NlsContexts.StatusBarText String nonLogText) {
        ApplicationManager.getApplication().assertIsDispatchThread();

        Project project = getActiveProject();
        Trinity<Notification, @NlsContexts.StatusBarText String, Long> statusMessage = EventLog.getStatusMessage(project);
        Alarm alarm = getAlarm();
        myCurrentNotification = StringUtil.isEmpty(nonLogText) && statusMessage != null && alarm != null ? statusMessage.first : null;

        if (alarm != null) {
            alarm.cancelAllRequests();
        }

        if (myCurrentNotification != null) {
            UIUtil.setCursor(myTextPanel, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            new Runnable() {
                @Override
                public void run() {
                    assert statusMessage != null;
                    String text = statusMessage.second;
                    if (myDirty || System.currentTimeMillis() - statusMessage.third >= DateFormatUtil.MINUTE) {
                        myTimeText = " (" + StringUtil.decapitalize(DateFormatUtil.formatPrettyDateTime(statusMessage.third)) + ")";
                        text += myTimeText;
                    } else {
                        myTimeText = null;
                    }
                    setStatusText(text);
                    alarm.addRequest(this, 30000);
                }
            }.run();
        } else {
            myTimeText = null;
            UIUtil.setCursor(myTextPanel, Cursor.getDefaultCursor());
            myDirty = true;
            setStatusText(nonLogText);
        }

        return myCurrentNotification != null;
    }

    private void setStatusText(@NlsContexts.StatusBarText String text) {
        myTextPanel.setText(text);
    }

    public String getText() {
        return myTextPanel.getText();
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new StatusPanel.AccessibleStatusPanel();
        }
        return accessibleContext;
    }

    protected class AccessibleStatusPanel extends JPanel.AccessibleJPanel {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.STATUS_BAR;
        }
    }
}