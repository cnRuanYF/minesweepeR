package com.ruanyf.minesweeper;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
	private JLabel lblTitle;
	private JButton btnStartEasy;
	private JButton btnStartNormal;
	private JButton btnStartHard;

	private JLabel[][] tiles;
	private JButton[][] covers;

	private Core core = new Core();

	// 开始游戏按钮事件
	private ActionListener btnActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == btnStartEasy) {
				startGame(Core.SIZE_EASY_ROW, Core.SIZE_EASY_COL, Core.SIZE_EASY_MINE);
			} else if (src == btnStartNormal) {
				startGame(Core.SIZE_NORMAL_ROW, Core.SIZE_NORMAL_COL, Core.SIZE_NORMAL_MINE);
			} else if (src == btnStartHard) {
				startGame(Core.SIZE_HARD_ROW, Core.SIZE_HARD_COL, Core.SIZE_HARD_MINE);
			}
		}
	};

	// 扫雷按钮事件
	private MouseAdapter sweepActionListener = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			Object obj = e.getSource();
			int btn = e.getButton();
			// 遍历所有按钮
			for (int row = 0; row < tiles.length; row++) {
				for (int col = 0; col < tiles[0].length; col++) {
					// 匹配发生点击事件的按钮
					if (obj == covers[row][col]) {
						if (btn == MouseEvent.BUTTON1) {
							open(row, col); // 左键翻开
						} else if (btn == MouseEvent.BUTTON3) {
							flag(row, col); // 右键标记
						}
						row = tiles.length; // 使条件失效以跳出循环
						break;
					}
				}
			}

			super.mouseClicked(e);
		}

	};

	/**
	 * Create the frame.
	 */
	public GUI() {
		// 设置窗口 (Frame)
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 640, 480);

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
		titlePane.setLayout(null);
		setContentPane(titlePane);

		// 标题
		lblTitle = new JLabel(TITLE, JLabel.CENTER);
		titlePane.add(lblTitle);
		lblTitle.setBounds(0, 100, 640, 100);
		lblTitle.setFont(new Font("微软雅黑", Font.PLAIN, 32));

		// 开始按钮
		btnStartEasy = new JButton();
		titlePane.add(btnStartEasy);
		btnStartEasy.setBounds(240, 240, 160, 50);
		btnStartEasy.addActionListener(btnActionListener);
		btnStartEasy.setText("Start");
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
		int tileSize = 48;
		int paddingHorizontal = 64;
		int paddingVertical = 64;

		// 调整尺寸
		Insets ins = getInsets(); // 获取窗口边框尺寸
		setSize(paddingHorizontal * 2 + tileSize * colSize + ins.left + ins.right, paddingVertical * 2 + tileSize * rowSize + ins.top + ins.bottom);

		// 初始化核心
		core.init(rowSize, colSize, mineSize);

		// 根据尺寸创建数组
		tiles = new JLabel[rowSize][colSize];
		covers = new JButton[rowSize][colSize];

		// 创建容器 (Panel)
		gamePane = new JPanel();
		gamePane.setLayout(null);
		setContentPane(gamePane);

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
			for (int r = 0; r < tiles.length; r++) {
				for (int c = 0; c < tiles[0].length; c++) {
					boolean isOpened = core.status[r][c] == Core.STAT_OPEN;
					tiles[r][c].setVisible(isOpened);
					covers[r][c].setVisible(!isOpened);

					// 游戏结束则标出雷的位置
					if (core.isGameover) {
						if (core.isMine(r, c)&&core.status[r][c] == Core.STAT_COVER ) {
							// 未标记的雷
							covers[r][c].setText(SYMBOL_MINE);
						} else if (core.isMine(r, c)&&core.status[r][c] == Core.STAT_FLAG ){
							// 标记正确的雷
							covers[r][c].setForeground(new Color(64, 128, 96));
						} else if(!core.isMine(r, c)&&core.status[r][c] == Core.STAT_FLAG){
							// 标记错误的雷
							covers[r][c].setForeground(Color.RED);
						}
					}
				}
			}

			// 游戏结束判定
			if (core.isGameover) {
				tiles[row][col].setOpaque(true);
				tiles[row][col].setBackground(Color.RED);
				JOptionPane.showMessageDialog(null, "  Gameover ...", TITLE, JOptionPane.ERROR_MESSAGE);
				showTitle();
			} else if (core.isWin) {
				JOptionPane.showMessageDialog(null, "  You Win !", TITLE, JOptionPane.PLAIN_MESSAGE);
				showTitle();
			}
		}
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
