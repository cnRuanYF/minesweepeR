package com.ruanyf.minesweeper;

/**
 * 扫雷游戏核心
 * 
 * @author Yaofeng Ruan
 */
public class Core {

	public static final String TITLE = "minesweepeR";

	public static final char MINE = 'M';

	// 默认参数常量
	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_NORMAL = 1;
	public static final int DIFFICULTY_HARD = 2;
	public static final int SIZE_EASY_ROW = 9;
	public static final int SIZE_EASY_COL = 9;
	public static final int SIZE_EASY_MINE = 10;
	public static final int SIZE_NORMAL_ROW = 16;
	public static final int SIZE_NORMAL_COL = 16;
	public static final int SIZE_NORMAL_MINE = 40;
	public static final int SIZE_HARD_ROW = 16;
	public static final int SIZE_HARD_COL = 30;
	public static final int SIZE_HARD_MINE = 99;

	// 格子状态常量
	public static final int STAT_OPEN = 0;
	public static final int STAT_COVER = 1;
	public static final int STAT_FLAG = 2;

	// 游戏状态
	public boolean isGameover;
	public boolean isWin;
	public int remain; // 待翻开格子数
	public int flags; // 标记数量

	// 格子
	public char[][] grids;
	public int[][] status;

	/**
	 * 使用默认参数初始化游戏 (简单)
	 */
	public void init() {
		init(DIFFICULTY_EASY);
	}

	/**
	 * 使用默认难度配置初始化游戏
	 * 
	 * @param difficulty
	 *            难度 (DIFFICULTY_*)
	 */
	public void init(int difficulty) {
		switch (difficulty) {
		case DIFFICULTY_EASY:
			init(SIZE_EASY_ROW, SIZE_EASY_COL, SIZE_EASY_MINE);
			break;
		case DIFFICULTY_NORMAL:
			init(SIZE_NORMAL_ROW, SIZE_NORMAL_COL, SIZE_NORMAL_MINE);
			break;
		case DIFFICULTY_HARD:
			init(SIZE_HARD_ROW, SIZE_HARD_COL, SIZE_HARD_MINE);
			break;
		}
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
	public void init(int rowSize, int colSize, int mineSize) {
		isGameover = false;
		isWin = false;
		remain = rowSize * colSize - mineSize;

		grids = new char[rowSize][colSize];
		status = new int[rowSize][colSize];

		// 随机生成地雷
		for (int i = 0; i < mineSize; i++) {
			int rCol;
			int rRow;
			do {
				rRow = (int) (Math.random() * rowSize);
				rCol = (int) (Math.random() * colSize);
			} while (isMine(rRow, rCol));
			grids[rRow][rCol] = MINE;
		}

		// 遍历所有格子
		for (int row = 0; row < grids.length; row++) {
			for (int col = 0; col < grids[row].length; col++) {
				// 计算
				if (!isMine(row, col)) {
					grids[row][col] = (char) (countAround(row, col) + '0');
				}
				// 盖住
				status[row][col] = STAT_COVER;
			}
		}

	}

	/**
	 * 判断指定行列是否为地雷
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 * @return 若指定行列为地雷则返回true
	 */
	public boolean isMine(int row, int col) {
		if (grids[row][col] == MINE) {
			return true;
		}
		return false;
	}
	
	/**
	 * 计算指定行列周围一圈地雷数量
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 * @return 指定行列周围一圈的地雷数量
	 */
	public int countAround(int row, int col) {
		// 行列偏移量 (位于↖,↑,↗,←,→,↙,↓,↘)
		int[] offsetR = { -1, -1, -1, 0, 0, 1, 1, 1 };
		int[] offsetC = { -1, 0, 1, -1, 1, -1, 0, 1 };

		int mineCount = 0;
		for (int i = 0; i < offsetR.length; i++) {
			int r = row + offsetR[i];
			int c = col + offsetC[i];
			boolean isOutOfBound = r < 0 || c < 0 || r >= grids.length || c >= grids[0].length;
			if (!isOutOfBound && isMine(r, c)) {
				mineCount++;
			}
		}
		return mineCount;
	}

	/**
	 * 翻开指定格子
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 */
	public void open(int row, int col) {
		status[row][col] = STAT_OPEN;
		remain--;

		// 如果翻到周围无雷的格子，则继续翻开周围一圈
		if (grids[row][col] == '0') {
			// 行列偏移量 (位于↖,↑,↗,←,→,↙,↓,↘)
			int[] offsetR = { -1, -1, -1, 0, 0, 1, 1, 1 };
			int[] offsetC = { -1, 0, 1, -1, 1, -1, 0, 1 };

			for (int i = 0; i < offsetR.length; i++) {
				int r = row + offsetR[i];
				int c = col + offsetC[i];

				// 翻开未越界且未翻开的
				boolean isOutOfBound = r < 0 || c < 0 || r >= grids.length || c >= grids[0].length;
				if (!isOutOfBound && status[r][c] == STAT_COVER) {
					open(r, c);
				}
			}
		}

		// 踩雷判定
		if (isMine(row, col)) {
			isGameover = true;
		}

		// 胜利判定
		if (remain == 0) {
			isWin = true;
		}
	}

	/**
	 * 标记指定格子
	 * 
	 * @param row
	 *            行索引 (从0起算)
	 * @param col
	 *            列索引 (从0起算)
	 */
	public void flag(int row, int col) {
		if (status[row][col] != STAT_OPEN) {
			if (status[row][col] == STAT_COVER) {
				status[row][col] = STAT_FLAG;
				flags++;
			} else {
				status[row][col] = STAT_COVER;
				flags--;
			}
		}
	}

	/**
	 * 翻开所有地雷格子 (不建议使用)
	 */
	@Deprecated
	public void showMines() {
		for (int row = 0; row < grids.length; row++) {
			for (int col = 0; col < grids[row].length; col++) {
				if (isMine(row, col)) {
					status[row][col] = STAT_OPEN;
				}
			}
		}
	}

}
