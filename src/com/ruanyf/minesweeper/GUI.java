package com.ruanyf.minesweeper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 扫雷GUI
 * 
 * @author Yaofeng Ruan
 */
@SuppressWarnings("serial")
public class GUI extends JFrame {

	public static final String TITLE = "minesweepeR";

	// 数字颜色 (1~8)
	public static final Color[] COLORS_NUM = { new Color(64, 80, 192), new Color(16, 102, 0), new Color(176, 0, 0), new Color(0, 0, 128), new Color(128, 0, 0), new Color(0, 128, 128), new Color(160, 0, 0), new Color(176, 0, 0) };

	// 预置主题色 ColorTheme
	public static final Color CT_RED = new Color(255, 192, 192);
	public static final Color CT_RED_FOCUS = new Color(255, 128, 128);
	public static final Color CT_GREEN = new Color(192, 255, 192);
	public static final Color CT_GREEN_FOCUS = new Color(128, 255, 128);
	public static final Color CT_BLUE = new Color(192, 224, 255);
	public static final Color CT_BLUE_FOCUS = new Color(128, 192, 255);
	public static final Color CT_PURPLE = new Color(224, 192, 255);
	public static final Color CT_PURPLE_FOCUS = new Color(192, 128, 255);

	// 图标资源路径
	public static final String PATH_IMG_MINE = "res/img/bomb.png";
	public static final String PATH_IMG_FLAG = "res/img/flag.png";
	public static final String PATH_IMG_FLAG_CORRECT = "res/img/flag_correct.png";
	public static final String PATH_IMG_FLAG_WRONG = "res/img/flag_wrong.png";

	// 主题色
	private Color themeColor;
	private Color themeFocusColor;

	// 图标
	public ImageIcon icMine, icFlag, icFlagCorrect, icFlagWrong;

	private JPanel titlePane;
	private JPanel gamePane;
	private JPanel menuPane;
	private JLabel lblTitle;

	private JLabel[][] tiles; // 数字格子
	private JButton[][] covers; // 砖块

	// 游戏核心
	private Core core = new Core();

	/**
	 * Create the frame.
	 */
	public GUI() {
		// 设置窗口 (Frame)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(640, 480));
		setSize(640, 480);
		setLocationRelativeTo(null); // 窗口居中

		// 显示标题界面
		showTitle();
	}

	/**
	 * 重新计算图标大小 (初始化时调用 / 自适应窗口改变砖块大小时调用)
	 */
	private void initImage(int width, int height) {
		icMine = new ImageIcon();
		icFlag = new ImageIcon();
		icFlagCorrect = new ImageIcon();
		icFlagWrong = new ImageIcon();

		ImageIcon[] icons = { icMine, icFlag, icFlagCorrect, icFlagWrong };
		String[] pathes = { PATH_IMG_MINE, PATH_IMG_FLAG, PATH_IMG_FLAG_CORRECT, PATH_IMG_FLAG_WRONG };

		for (int i = 0; i < icons.length; i++) {
			ImageIcon ic = new ImageIcon(pathes[i]);
			icons[i].setImage(ic.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
		}
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
//					themeColor = CT_GREEN;
//					themeFocusColor = CT_GREEN_FOCUS;
					themeColor = CT_PURPLE;
					themeFocusColor = CT_PURPLE_FOCUS;
					startGame(Core.SIZE_EASY_ROW, Core.SIZE_EASY_COL, Core.SIZE_EASY_MINE);
				} else if (btnTexts[1].equals(cmd)) {
					themeColor = CT_BLUE;
					themeFocusColor = CT_BLUE_FOCUS;
					startGame(Core.SIZE_NORMAL_ROW, Core.SIZE_NORMAL_COL, Core.SIZE_NORMAL_MINE);
				} else if (btnTexts[2].equals(cmd)) {
					themeColor = CT_RED;
					themeFocusColor = CT_RED_FOCUS;
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
		revalidate(); // 刷新UI
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
		setLocationRelativeTo(null); // 窗口居中

		// 初始化图标
		initImage(tileSize, tileSize);

		// 初始化核心
		core.init(rowSize, colSize, mineSize);

		// 扫雷按钮事件
		MouseAdapter sweepActionListener = new MouseAdapter() {

			static final int BTN_L = MouseEvent.BUTTON1_DOWN_MASK;
			static final int BTN_M = MouseEvent.BUTTON2_DOWN_MASK;
			static final int BTN_R = MouseEvent.BUTTON3_DOWN_MASK;
			static final int BTN_L_R = MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.BUTTON3_DOWN_MASK;

			static final int STAT_NORMAL = 0;
			static final int STAT_FOCUS = 1;
			static final int STAT_DOWM = 2;

			int mask; // 记录当前按键

			/*
			 * 由于mouseReleased(鼠标抬起事件)中获取到的事件源为mousePressed(鼠标按下事件)被触发时的事件源，
			 * 如：按下“按钮A”，将鼠标移动到“按钮B”，松开按键，此时触发的依然是“按钮A”的事件
			 * 而扫雷的需求是：要翻开的格子，以松开鼠标时鼠标所在的格子为准 所以在此定义一个变量用于存储鼠标最后一次进入的控件
			 */
			Object focusComponent; // 看上面↑

			@Override
			public void mouseEntered(MouseEvent e) {
				focusComponent = e.getSource(); // 鼠标进入控件时标记为当前控件

				if (focusComponent instanceof JButton) {
					if (mask == BTN_L || mask == BTN_M || mask == BTN_L_R) {
						// 鼠标按键进行“翻开”相关操作的情形
						setGridStyle((JButton) focusComponent, STAT_DOWM);
					} else {
						setGridStyle((JButton) focusComponent, STAT_FOCUS);
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				focusComponent = null; // 鼠标离开控件时清除当前控件
										// (否则在场景外松开鼠标依然会触发位于边缘的格子的事件)
				Object eSrc = e.getSource();
				if (eSrc instanceof JButton) {
					setGridStyle((JButton) eSrc, STAT_NORMAL);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mask = e.getModifiersEx(); // 按下鼠标时记录鼠标按键

				if (focusComponent instanceof JButton) {
					if (mask == BTN_L || mask == BTN_M || mask == BTN_L_R) {
						// 鼠标按键进行“翻开”相关操作的情形
						setGridStyle((JButton) focusComponent, STAT_DOWM);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// 遍历所有按钮
				boolean flag = true; // 是否继续遍历的标志
				for (int row = 0; flag && row < tiles.length; row++) {
					for (int col = 0; flag && col < tiles[0].length; col++) {

						// 匹配发生点击事件的按钮
						if (focusComponent == covers[row][col] && mask == BTN_L) {
							open(row, col); // 左键翻开
							flag = false;
						} else if (focusComponent == covers[row][col] && mask == BTN_R) {
							flag(row, col); // 右键标记
							flag = false;
						} else if (focusComponent == tiles[row][col] && (mask == BTN_L_R || mask == BTN_M)) {
							sweep(row, col); // 左键+右键快速排雷(或中键)
							flag = false;
						}
					}
				}
				mask = 0; // 重置按键标识
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
					tiles[r][c].setText("");
					tiles[r][c].setIcon(icMine);
				}
				tiles[r][c].setVisible(false);

				// 盖子 (按钮)
				covers[r][c] = new JButton();
				covers[r][c].setBounds(paddingHorizontal + tileSize * c, paddingVertical + tileSize * r, tileSize, tileSize);
				setGridStyle(covers[r][c], 0);
				gamePane.add(covers[r][c]);

				// 绑定事件
				covers[r][c].addMouseListener(sweepActionListener);
				tiles[r][c].addMouseListener(sweepActionListener);
			}
		}

		setContentPane(gamePane);
		revalidate(); // 刷新UI
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
				covers[row][col].setIcon(icFlag);
			} else {
				covers[row][col].setIcon(null);
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
	 * 为格子设置预定义的样式
	 * 
	 * @param component
	 *            控件对象
	 * @param status
	 *            控件状态 (0:普通状态, 1:焦点状态, 2:按下状态)
	 */
	private void setGridStyle(JComponent component, int status) {
		switch (status) {
		case 1:
			component.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			component.setBackground(themeFocusColor);
			break;
		case 2:
			component.setBorder(BorderFactory.createLoweredSoftBevelBorder());
			component.setBackground(themeColor);
			break;
		case 0:
		default:
			component.setBorder(BorderFactory.createRaisedSoftBevelBorder());
			component.setBackground(themeColor);
			break;
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
						covers[r][c].setIcon(icMine);
					} else if (core.isMine(r, c) && core.status[r][c] == Core.STAT_FLAG) {
						// 标记正确的雷
						covers[r][c].setIcon(icFlagCorrect);
					} else if (!core.isMine(r, c) && core.status[r][c] == Core.STAT_FLAG) {
						// 标记错误的雷
						covers[r][c].setIcon(icFlagWrong);
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
