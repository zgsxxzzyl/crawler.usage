package p;

import p.a.CommonConfig;
import p.a.GoodType;
import p.b.DD373_D2R_Search;
import p.b.HtmlD2rPipelineForm;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchPage extends JFrame {
    private JComboBox<GoodType> goodTypeComboBox;
    private JTextField keyword1Field;
    private JTextArea keyword2Area;
    private JButton submitButton;
    private JButton cancelButton; // 取消按钮
    private JTextArea logArea; // 日志输出区域
    private PrintWriter logWriter; // 日志写入器
    private Spider currentSpider; // 当前正在运行的爬虫实例
    private ExecutorService executorService; // 线程池
    private JComboBox<String> matchModeComboBox; // 匹配模式下拉框
    private JCheckBox toLowerCaseCheckBox; // 是否转换为小写复选框
    private JTextField resultField; // 结果文本输入框
    private JButton viewResultButton; // 查看结果按钮

    public SearchPage() {
        initializeComponents();
        setupLayout();
        addEventListeners();
        setupFrame();
        setupLogging(); // 初始化日志系统
        executorService = Executors.newSingleThreadExecutor(); // 初始化线程池
    }

    private void initializeComponents() {
        // 设置组件字体
        Font inputFont = new Font("微软雅黑", Font.PLAIN, 13);

        // 加载商品类型配置
        GoodType.loadGoodTypes();

        // 下拉选择框 - 商品类型（使用配置文件加载的数据，默认选择"全部"）
        goodTypeComboBox = new JComboBox<>(GoodType.getGoodTypeArray());
        GoodType defaultType = GoodType.valueOf("ALL");
        if (defaultType != null) {
            goodTypeComboBox.setSelectedItem(defaultType);
        }
        goodTypeComboBox.setRenderer(new GoodTypeRenderer());
        goodTypeComboBox.setFont(inputFont);
        goodTypeComboBox.setPreferredSize(new Dimension(150, 30));
        // 保持系统默认边框，确保与系统风格一致
        goodTypeComboBox.setBorder(UIManager.getBorder("ComboBox.border"));

        // 输入框1 - 关键词1
        keyword1Field = new JTextField(20);
        keyword1Field.setFont(inputFont);
        keyword1Field.setPreferredSize(new Dimension(150, 30));
        // 使用系统默认边框，确保与系统风格一致
        keyword1Field.setBorder(UIManager.getBorder("TextField.border"));

        // 文本域 - 关键词2
        keyword2Area = new JTextArea(5, 20);
        keyword2Area.setFont(inputFont);
        keyword2Area.setLineWrap(true);
        keyword2Area.setWrapStyleWord(true);
        // 使用系统默认边框，确保与系统风格一致
        keyword2Area.setBorder(UIManager.getBorder("TextArea.border"));

        // 匹配模式下拉框
        matchModeComboBox = new JComboBox<>(new String[]{"全匹配", "任一匹配"});
        matchModeComboBox.setSelectedIndex(0); // 默认选择全匹配
        matchModeComboBox.setFont(inputFont);
        matchModeComboBox.setPreferredSize(new Dimension(150, 30));
        matchModeComboBox.setBorder(UIManager.getBorder("ComboBox.border"));

        // 是否转换为小写复选框
        toLowerCaseCheckBox = new JCheckBox("转换为小写");
        toLowerCaseCheckBox.setSelected(true); // 默认选中
        toLowerCaseCheckBox.setFont(inputFont);

        // 结果文本输入框
        resultField = new JTextField(20);
        resultField.setFont(inputFont);
        resultField.setPreferredSize(new Dimension(150, 30));
        resultField.setBorder(UIManager.getBorder("TextField.border"));

        // 提交按钮 - 使用蓝色主题
        submitButton = new JButton("搜索");
        styleButton(submitButton, new Color(70, 130, 180), new Color(100, 149, 237));

        // 取消按钮 - 使用相同蓝色主题
        cancelButton = new JButton("取消搜索");
        styleButton(cancelButton, new Color(70, 130, 180), new Color(100, 149, 237));
        cancelButton.setEnabled(false); // 默认禁用

        // 查看结果按钮 - 使用相同蓝色主题
        viewResultButton = new JButton("查看结果");
        styleButton(viewResultButton, new Color(70, 130, 180), new Color(100, 149, 237));
        viewResultButton.setEnabled(false); // 默认禁用

        // 日志输出区域
        logArea = new JTextArea(10, 50);
        logArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(245, 245, 245));
    }

    // 自定义渲染器，用于在下拉框中显示中文名称
    private static class GoodTypeRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof GoodType) {
                setText(((GoodType) value).getName());
            }
            return this;
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // 创建主面板
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        // 标题
        JLabel titleLabel = new JLabel("商品搜索");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        titleLabel.setForeground(new Color(70, 130, 180));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);

        // 输入面板
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "搜索条件",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14),
                new Color(70, 130, 180)));

        GridBagConstraints inputGbc = new GridBagConstraints();
        inputGbc.insets = new Insets(8, 8, 8, 8);
        inputGbc.fill = GridBagConstraints.HORIZONTAL;

        // 统一标签尺寸
        Dimension labelSize = new Dimension(80, 30);

        // 第一行 - 商品类型和关键词1
        JLabel typeLabel = new JLabel("商品类型:");
        typeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        typeLabel.setPreferredSize(labelSize);
        typeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        inputGbc.gridx = 0;
        inputGbc.gridy = 0;
        inputGbc.weightx = 0;
        inputGbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(typeLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.weightx = 0.3;
        inputPanel.add(goodTypeComboBox, inputGbc);

        JLabel keyword1Label = new JLabel("关键词1:");
        keyword1Label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        keyword1Label.setPreferredSize(labelSize);
        keyword1Label.setHorizontalAlignment(SwingConstants.RIGHT);
        inputGbc.gridx = 2;
        inputGbc.weightx = 0;
        inputPanel.add(keyword1Label, inputGbc);

        inputGbc.gridx = 3;
        inputGbc.weightx = 0.3;
        inputPanel.add(keyword1Field, inputGbc);

        // 第二行 - 关键词2
        JLabel keyword2Label = new JLabel("关键词2:");
        keyword2Label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        keyword2Label.setPreferredSize(labelSize);
        keyword2Label.setHorizontalAlignment(SwingConstants.RIGHT);
        inputGbc.gridx = 0;
        inputGbc.gridy = 1;
        inputGbc.weightx = 0;
        inputGbc.anchor = GridBagConstraints.NORTHWEST;
        inputPanel.add(keyword2Label, inputGbc);

        // 创建文本域面板，确保尺寸统一
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setPreferredSize(new Dimension(200, 100));
        JScrollPane scrollPane = new JScrollPane(keyword2Area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // 使用系统默认边框，确保与系统风格一致
        scrollPane.setBorder(UIManager.getBorder("ScrollPane.border"));
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);

        inputGbc.gridx = 1;
        inputGbc.gridwidth = 3;
        inputGbc.weightx = 1.0;
        inputGbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(textAreaPanel, inputGbc);

        // 第三行 - 匹配模式和文本处理
        inputGbc.gridy = 2;
        inputGbc.gridwidth = 1;
        inputGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel matchModeLabel = new JLabel("匹配模式:");
        matchModeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        matchModeLabel.setPreferredSize(labelSize);
        matchModeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        inputGbc.gridx = 0;
        inputGbc.weightx = 0;
        inputPanel.add(matchModeLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.weightx = 0.3;
        inputPanel.add(matchModeComboBox, inputGbc);

        JLabel toLowerCaseLabel = new JLabel("文本处理:");
        toLowerCaseLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        toLowerCaseLabel.setPreferredSize(labelSize);
        toLowerCaseLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        inputGbc.gridx = 2;
        inputGbc.weightx = 0;
        inputPanel.add(toLowerCaseLabel, inputGbc);

        inputGbc.gridx = 3;
        inputGbc.weightx = 0.3;
        inputGbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(toLowerCaseCheckBox, inputGbc);

        // 第四行 - 结果输入框
        inputGbc.gridy = 3;
        inputGbc.gridwidth = 1;
        inputGbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel resultLabel = new JLabel("结果:");
        resultLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        resultLabel.setPreferredSize(labelSize);
        resultLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        inputGbc.gridx = 0;
        inputGbc.weightx = 0;
        inputPanel.add(resultLabel, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.gridwidth = 3;
        inputGbc.weightx = 1.0;
        inputPanel.add(resultField, inputGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(inputPanel, gbc);

        // 按钮面板（只添加一次）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton); // 添加取消按钮
        buttonPanel.add(viewResultButton); // 添加查看结果按钮

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // 日志面板
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "运行日志",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14),
                new Color(70, 130, 180)));
        logPanel.setPreferredSize(new Dimension(600, 200));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScrollPane.setBorder(UIManager.getBorder("ScrollPane.border"));
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        add(logPanel, BorderLayout.SOUTH);
    }


    private void addEventListeners() {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancel();
            }
        });
        viewResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewResult();
            }
        });

    }

    private void handleSubmit() {
        // 获取选中的商品类型枚举
        GoodType selectedGoodType = (GoodType) goodTypeComboBox.getSelectedItem();

        String keyword1 = keyword1Field.getText();
        String keyword2 = keyword2Area.getText();
        // 获取匹配模式
        boolean allMatch = matchModeComboBox.getSelectedIndex() == 0; // 0为全匹配，1为任一匹配
        // 获取是否转换为小写
        boolean toLowerCase = toLowerCaseCheckBox.isSelected();

        // 直接调用 search 方法，传递表单的5个值，无需二次确认
        search(selectedGoodType, keyword1, keyword2, allMatch, toLowerCase);
    }

    private void handleCancel() {
        if (currentSpider != null && currentSpider.getStatus() == Spider.Status.Running) {
            log("用户请求取消搜索任务...");
            currentSpider.stop();
            log("搜索任务已取消");
            updateButtonState(false); // 更新按钮状态
        }
    }

    /**
     * 搜索方法，接收表单的5个参数
     *
     * @param goodType    商品类型枚举
     * @param keyword1    关键词1
     * @param keyword2    关键词2
     * @param allMatch    匹配模式
     * @param toLowerCase 是否转换为小写
     */
    private void search(GoodType goodType, String keyword1, String keyword2, boolean allMatch, boolean toLowerCase) {
        // 在后台线程中执行搜索任务
        executorService.submit(() -> {
            executeSearch(goodType, keyword1, keyword2, allMatch, toLowerCase);
        });
    }

    /**
     * 实际执行搜索任务的方法
     *
     * @param goodType    商品类型枚举
     * @param keyword1    关键词1
     * @param keyword2    关键词2
     * @param allMatch    匹配模式
     * @param toLowerCase 是否转换为小写
     */
    private void executeSearch(GoodType goodType, String keyword1, String keyword2, boolean allMatch, boolean toLowerCase) {
        SwingUtilities.invokeLater(() -> {
            updateButtonState(true); // 更新按钮状态为搜索中
        });

        // 记录搜索开始日志
        log("开始执行搜索操作: " + goodType.getName() + ", 关键词1: " + keyword1 +
                ", 匹配模式: " + (allMatch ? "全匹配" : "任一匹配") +
                ", 转换小写: " + (toLowerCase ? "是" : "否"));

        // 这里可以添加实际的搜索逻辑
        // 例如调用网络爬虫或其他搜索功能
        // 实际调用示例:
        String url = String.format(goodType.getUrl(), 1, keyword1);
        log("构建搜索URL: " + url);

        try {
            // 将keyword2分割成数组并传递给爬虫
            String[] keyword2Array = keyword2.split("[\\r\\n]+"); // 按行分割

            // 设置匹配模式和小写转换
            DD373_D2R_Search.allMatch = allMatch;
            DD373_D2R_Search.toLowerCase = toLowerCase;

            // 设置SearchPage引用，使爬虫可以输出日志到界面
            DD373_D2R_Search.setSearchPage(this);

            currentSpider = Spider.create(new DD373_D2R_Search(goodType, keyword1, keyword2Array)).addUrl(url)
                    .addPipeline(new ConsolePipeline())
                    .addPipeline(new HtmlD2rPipelineForm(CommonConfig.DATA_PATH, keyword1, keyword2Array, this)).thread(5);

            log("开始执行网络爬虫任务...");
            currentSpider.run();
            log("搜索任务执行完成");
        } catch (Exception e) {
            log("搜索过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> {
                updateButtonState(false); // 搜索完成后更新按钮状态
                currentSpider = null;
            });
        }
    }

    /**
     * 更新按钮状态
     *
     * @param isSearching 是否正在搜索
     */
    private void updateButtonState(boolean isSearching) {
        submitButton.setEnabled(!isSearching);
        cancelButton.setEnabled(isSearching);
        viewResultButton.setEnabled(!isSearching && resultField.getText() != null && !resultField.getText().trim().isEmpty());

        if (isSearching) {
            submitButton.setText("搜索中...");
            cancelButton.setText("取消搜索");
        } else {
            submitButton.setText("搜索");
            cancelButton.setText("取消搜索");
        }
    }


    /**
     * 初始化日志系统
     */
    private void setupLogging() {
        // 创建自定义的PrintWriter，将日志同时输出到日志区域和控制台
        logWriter = new PrintWriter(new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                String text = new String(cbuf, off, len);
                appendToLogArea(text);
            }

            @Override
            public void flush() throws IOException {
                // 不需要特殊处理
            }

            @Override
            public void close() throws IOException {
                // 不需要特殊处理
            }
        }, true);
    }

    /**
     * 向日志区域添加日志信息
     *
     * @param message 日志信息
     */
    private void appendToLogArea(String message) {
        SwingUtilities.invokeLater(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            String logMessage = "[" + timestamp + "] " + message;

            logArea.append(logMessage);
            logArea.setCaretPosition(logArea.getDocument().getLength()); // 自动滚动到最新日志
        });
    }

    /**
     * 记录日志信息
     *
     * @param message 日志信息
     */
    public void log(String message) {
        logWriter.println(message);
    }

    private void setupFrame() {
        setTitle("DD373扫货工具v0.0.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // 允许调整窗口大小
        setBackground(Color.WHITE);

        // 设置窗口图标（如果有的话）
        // setIconImage(new ImageIcon("path/to/icon.png").getImage());

        pack();
        setLocationRelativeTo(null);

        // 添加窗口装饰效果
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置结果文本框的内容
     *
     * @param result 结果路径
     */
    public void setResultText(String result) {
        SwingUtilities.invokeLater(() -> {
            resultField.setText(result);
            // 更新查看结果按钮状态
            viewResultButton.setEnabled(result != null && !result.trim().isEmpty());
        });
    }

    private void handleViewResult() {
        String resultPath = resultField.getText();
        if (resultPath != null && !resultPath.trim().isEmpty()) {
            File file = new File(resultPath.trim());
            if (file.exists()) {
                try {
                    // 使用系统默认程序打开文件
                    Desktop.getDesktop().open(file);
                    log("正在打开结果文件: " + file.getAbsolutePath());
                } catch (Exception e) {
                    log("打开文件时出错: " + e.getMessage());
                    JOptionPane.showMessageDialog(this, "无法打开文件: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                log("结果文件不存在: " + resultPath);
                JOptionPane.showMessageDialog(this, "文件不存在: " + resultPath, "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            log("结果路径为空");
            JOptionPane.showMessageDialog(this, "请先执行搜索获取结果文件路径", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * 为按钮设置统一样式
     *
     * @param button     按钮组件
     * @param baseColor  基础颜色
     * @param hoverColor 悬停颜色
     */
    private void styleButton(JButton button, Color baseColor, Color hoverColor) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorderPainted(false);
        button.setOpaque(true);

        // 定义禁用状态的颜色
        Color disabledBgColor = new Color(
                Math.max(0, baseColor.getRed() - 30),
                Math.max(0, baseColor.getGreen() - 30),
                Math.max(0, baseColor.getBlue() - 30)
        );

        // 添加属性改变监听器，正确处理启用/禁用状态变化
        button.addPropertyChangeListener("enabled", evt -> {
            if (button.isEnabled()) {
                // 启用状态：恢复原始样式
                button.setBackground(baseColor);
                button.setForeground(Color.WHITE);
            } else {
                // 禁用状态：使用禁用样式
                button.setBackground(disabledBgColor);
                button.setForeground(Color.LIGHT_GRAY);
            }
        });

        // 添加鼠标事件监听器
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(baseColor.darker());
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }
        });
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SearchPage().setVisible(true);
            }
        });
    }
}
