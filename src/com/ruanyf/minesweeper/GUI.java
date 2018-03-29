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

/**
 * 扫雷GUI
 * 
 * @author Yaofeng Ruan
 */
public class GUI extends JFrame {

	public static final String TITLE = "minesweepeR";

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
	private ActionListener sweepActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			// 遍历所有按钮
			for (int row = 0; row < tiles.length; row++) {
				for (int col = 0; col < tiles[0].length; col++) {
					// 匹配发生点击事件的按钮
					if (obj == covers[row][col]) {
						open(row, col);
						row = tiles.length; // 使条件失效以跳出循环
						break;
					}
				}
			}
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

		// 颜色 (1~8)
		Color[] colors = { new Color(64, 80, 192), new Color(16, 102, 0), new Color(176, 0, 0), new Color(0, 0, 128), new Color(128, 0, 0), new Color(0, 128, 128), new Color(160, 0, 0), new Color(176, 0, 0) };

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
					;
				} else if (num > 0 && num < 9) {
					tiles[r][c].setForeground(colors[num - 1]);
				} else {
					tiles[r][c].setOpaque(true);
					tiles[r][c].setBackground(Color.yellow);
				}
				tiles[r][c].setVisible(false);

				// 盖子 (按钮)
				covers[r][c] = new JButton();
				gamePane.add(covers[r][c]);
				covers[r][c].setBounds(paddingHorizontal + tileSize * c, paddingVertical + tileSize * r, tileSize, tileSize);
				covers[r][c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
				covers[r][c].addActionListener(sweepActionListener);
			}
		}
	}

	/**
	 * 翻开格子
	 * 
	 * @param row
	 * @param col
	 */
	private void open(int row, int col) {
		core.open(row, col);

		setTitle("remain: " + core.remain);

		// 刷新所有格子状态
		for (int r = 0; r < tiles.length; r++) {
			for (int c = 0; c < tiles[0].length; c++) {
				boolean isOpened = core.status[r][c] == Core.STAT_OPEN;
				tiles[r][c].setVisible(isOpened);
				covers[r][c].setVisible(!isOpened);

				// 游戏结束则标出雷的位置
				if (core.isGameover && core.isMine(r, c)) {
					covers[r][c].setText("" + Core.MINE);
				}
			}
		}

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
