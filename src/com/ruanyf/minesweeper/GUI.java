package com.ruanyf.minesweeper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 扫雷GUI
 * 
 * @author Yaofeng Ruan
 */
public class GUI extends JFrame {

	public static final String TITLE = "minesweepeR";
	public static final String SYMBOL_MINE = "💀";
	public static final String SYMBOL_FLAG = "🚩";

	// 数字颜色 (1~8)
	public static final Color[] COLORS_NUM = { new Color(64, 80, 192), new Color(16, 102, 0), new Color(176, 0, 0), new Color(0, 0, 128), new Color(128, 0, 0), new Color(0, 128, 128), new Color(160, 0, 0), new Color(176, 0, 0) };

	private JPanel titlePane;
	private JPanel gamePane;
	private JPanel menuPane;
	private JLabel lblTitle;

	private JLabel[][] tiles;
	private JButton[][] covers;

	private Core core = new Core();

	/**
	 * Create the frame.
	 */
	public GUI() {
		// 设置窗口 (Frame)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(640, 480));
		setSize(640, 480);
		
		// 窗口居中
		int scrWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int scrHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		setLocation(scrWidth / 2 - getWidth() / 2, scrHeight / 2 - getHeight() / 2);

		// 显示标题界面
		showTitle();
	}

	/**
	 * 显示标题界面
	 */
	private void showTitle() {

		setTitle(TITLE);

		// 设置容器 (Panel)
		titlePane = new JPanel();
		titlePane.setBackground(Color.WHITE);
		titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.Y_AXIS));

		// 标题
		lblTitle = new JLabel(TITLE, JLabel.CENTER);
		lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTitle.setPreferredSize(new Dimension(0, 128));
		lblTitle.setFont(new Font(null, Font.ITALIC, 48));

		// 按钮的容器
		menuPane = new JPanel();
		menuPane.setOpaque(false);
		menuPane.setLayout(new BoxLayout(menuPane, BoxLayout.X_AXIS));

		// 开始按钮的属性定义
		Font btnFont = new Font(null, Font.PLAIN, 24);
		String[] btnTexts = new String[] { "Easy", "Normal", "Hard" };
		Color[] btnColors = new Color[] { new Color(0, 192, 0), new Color(0, 160, 255), new Color(255, 64, 0) };
		JButton[] btns = new JButton[btnTexts.length];

		// 开始按钮的事件
		ActionListener btnActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (btnTexts[0].equals(cmd)) {
					startGame(Core.SIZE_EASY_ROW, Core.SIZE_EASY_COL, Core.SIZE_EASY_MINE);
				} else if (btnTexts[1].equals(cmd)) {
					startGame(Core.SIZE_NORMAL_ROW, Core.SIZE_NORMAL_COL, Core.SIZE_NORMAL_MINE);
				} else if (btnTexts[2].equals(cmd)) {
					startGame(Core.SIZE_HARD_ROW, Core.SIZE_HARD_COL, Core.SIZE_HARD_MINE);
				}
			}
		};

		// 设定按钮
		for (int i = 0; i < btns.length; i++) {
			// 实例化
			btns[i] = new JButton();

			// 设置尺寸
			btns[i].setMinimumSize(new Dimension(128, 128));
			btns[i].setMaximumSize(new Dimension(128, 128));
			btns[i].setPreferredSize(new Dimension(128, 128));

			// 前景(文字)颜色
			btns[i].setBackground(btnColors[i]);
			btns[i].setForeground(Color.WHITE);
			btns[i].setFont(btnFont);
			btns[i].setText(btnTexts[i]);

			// 事件监听
			btns[i].addActionListener(btnActionListener);

			// 添加按钮和占位元素到面板
			menuPane.add(btns[i]);
			menuPane.add(Box.createHorizontalStrut(32)); // 占位元素
		}

		// 移除最后一个多余的占位元素 (Strut)
		menuPane.remove(menuPane.getComponentCount() - 1);

		// 添加组件到TitlePane
		titlePane.add(Box.createVerticalGlue()); // 自适应占位元素
		titlePane.add(lblTitle);
		titlePane.add(menuPane);
		titlePane.add(Box.createVerticalGlue());

		setContentPane(titlePane);
		// setContentPane之后有时界面不更新，需要更改窗口大小来强制重绘 (BUG?)
		setSize(getWidth() + 1, getHeight() + 1);
		setSize(getWidth() - 1, getHeight() - 1);
	}

	/**
	 * 使用指定参数初始化游戏
	 * 
	 * @param rowSize
	 *            格子行数 (高度)
	 * @param colSize
	 *            格子列数 (宽度)
	 * @param mineSize
	 *            地雷的数量
	 */
	private void startGame(int rowSize, int colSize, int mineSize) {
		// UI尺寸
		int tileSize = 32;
		int paddingHorizontal = 64;
		int paddingVertical = 64;

		// 调整尺寸
		Insets ins = getInsets(); // 获取窗口边框尺寸
		setSize(paddingHorizontal * 2 + tileSize * colSize + ins.left + ins.right, paddingVertical * 2 + tileSize * rowSize + ins.top + ins.bottom);

		// 初始化核心
		core.init(rowSize, colSize, mineSize);

		// 扫雷按钮事件
		MouseAdapter sweepActionListener = new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Object obj = e.getSource();
				int btn = e.getButton();

				// 遍历所有按钮
				boolean flag = true;
				for (int row = 0; flag && row < tiles.length; row++) {
					for (int col = 0; flag && col < tiles[0].length; col++) {

						// 匹配发生点击事件的按钮
						if (obj == covers[row][col] && btn == MouseEvent.BUTTON1) {
							open(row, col); // 左键翻开
							flag = false;
						} else if (obj == covers[row][col] && btn == MouseEvent.BUTTON3) {
							flag(row, col); // 右键标记
							flag = false;
						} else if (obj == tiles[row][col] && btn == MouseEvent.BUTTON2) {
							sweep(row, col); // 中键快速排雷
							flag = false;
						}
					}
				}

				super.mouseClicked(e);
			}

		};

		// 根据尺寸创建数组
		tiles = new JLabel[rowSize][colSize];
		covers = new JButton[rowSize][colSize];

		// 创建容器 (Panel)
		gamePane = new JPanel();
		gamePane.setLayout(null);

		// 创建控件
		for (int r = 0; r < rowSize; r++) {
			for (int c = 0; c < colSize; c++) {
				// 格子
				int num = core.grids[r][c] - '0';
				tiles[r][c] = new JLabel("" + core.grids[r][c], JLabel.CENTER);
				gamePane.add(tiles[r][c]);
				tiles[r][c].setBounds(paddingHorizontal + tileSize * c, paddingVertical + tileSize * r, tileSize, tileSize);
				tiles[r][c].setBorder(BorderFactory.createLoweredSoftBevelBorder());
				tiles[r][c].setFont(new Font(null, Font.BOLD, tileSize * 3 / 4));
				if (num == 0) {
					tiles[r][c].setText("");
				} else if (num > 0 && num < 9) {
					tiles[r][c].setForeground(COLORS_NUM[num - 1]);
				} else if (core.grids[r][c] == Core.MINE) {
					tiles[r][c].setText(SYMBOL_MINE);
				}
				tiles[r][c].setVisible(false);

				// 盖子 (按钮)
				covers[r][c] = new JButton();
				gamePane.add(covers[r][c]);
				covers[r][c].setBounds(paddingHorizontal + tileSize * c, paddingVertical + tileSize * r, tileSize, tileSize);
				covers[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
				covers[r][c].setFont(new Font(null, Font.BOLD, tileSize * 3 / 4));
				covers[r][c].addMouseListener(sweepActionListener);
				tiles[r][c].addMouseListener(sweepActionListener);
			}
		}

		setContentPane(gamePane);
		// setContentPane之后有时界面不更新，需要更改窗口大小来强制重绘 (BUG?)
		setSize(getWidth() + 1, getHeight() + 1);
		setSize(getWidth() - 1, getHeight() - 1);
	}

	/**
	 * 标记格子
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 */
	private void flag(int row, int col) {

		// 未翻开才可标记
		if (core.status[row][col] != Core.STAT_OPEN) {
			core.flag(row, col);

			setTitle(TITLE + " ( Remain: " + core.remain + " , Flag: " + core.flags + " )");

			// 刷新格子状态
			if (core.status[row][col] == Core.STAT_FLAG) {
				covers[row][col].setText(SYMBOL_FLAG);
			} else {
				covers[row][col].setText("");
			}
		}
	}

	/**
	 * 翻开格子
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 */
	private void open(int row, int col) {

		// 没有标记且未翻开才可翻开
		if (core.status[row][col] == Core.STAT_COVER) {
			core.open(row, col);

			setTitle(TITLE + " ( Remain: " + core.remain + " , Flag: " + core.flags + " )");

			// 刷新所有格子状态
			refreshStatus();
		}
	}

	/**
	 * 快速排雷 (若周边雷数与周边标记数一致，则翻开周边未标记的格子)
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 */
	private void sweep(int row, int col) {

		// 已经翻开才可以对周边进行快速排雷
		if (core.status[row][col] == Core.STAT_OPEN) {
			core.sweep(row, col);

			setTitle(TITLE + " ( Remain: " + core.remain + " , Flag: " + core.flags + " )");

			// 刷新所有格子状态
			refreshStatus();
		}
	}

	/**
	 * 刷新所有格子并更新游戏状态
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 */
	private void refreshStatus() {

		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[0].length; c++) {
				boolean isOpened = core.status[r][c] == Core.STAT_OPEN;
				tiles[r][c].setVisible(isOpened);
				covers[r][c].setVisible(!isOpened);

				// 游戏结束则标出雷的位置
				if (core.isGameover) {
					if (core.isMine(r, c) && core.status[r][c] == Core.STAT_COVER) {
						// 未标记的雷
						covers[r][c].setText(SYMBOL_MINE);
					} else if (core.isMine(r, c) && core.status[r][c] == Core.STAT_FLAG) {
						// 标记正确的雷
						covers[r][c].setForeground(new Color(64, 128, 96));
					} else if (!core.isMine(r, c) && core.status[r][c] == Core.STAT_FLAG) {
						// 标记错误的雷
						covers[r][c].setForeground(Color.RED);
					}
				}
			}
		}

		// 游戏结束判定
		if (core.isGameover) {
			tiles[core.deadRow][core.deadCol].setOpaque(true);
			tiles[core.deadRow][core.deadCol].setBackground(Color.RED);
			JOptionPane.showMessageDialog(null, "  Gameover ...", TITLE, JOptionPane.ERROR_MESSAGE);
			showTitle();
		} else if (core.isWin) {
			JOptionPane.showMessageDialog(null, "  You Win !", TITLE, JOptionPane.PLAIN_MESSAGE);
			showTitle();
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
