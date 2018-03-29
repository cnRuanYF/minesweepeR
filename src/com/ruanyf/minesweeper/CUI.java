package com.ruanyf.minesweeper;

import java.util.Scanner;

/**
 * 控制台扫雷
 * 
 * @author Yaofeng Ruan
 */
public class CUI {

	public static final char EMPTY = ' ';
	public static final char COVERED = '#';

	public static final boolean ISDEBUG = false;

	private Core core = new Core();

	/**
	 * 应用程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CUI cui = new CUI();
		cui.start();
	}

	/**
	 * 开始游戏
	 */
	public void start() {
		core.init();

		Scanner sc = new Scanner(System.in);

		while (!core.isGameover) {
			print(ISDEBUG);

			System.out.print("Input \"row col\" to sweep > ");
			int row = sc.nextInt();
			int col = sc.nextInt();
			core.open(row, col);
			
			// 失败判定
			if (core.isGameover) {
				print(ISDEBUG);
				gameover();
			}
			
			// 胜利判定
			if (core.isWin) {
				print(ISDEBUG);
				gamewin();
			}
		}

		sc.close();
	}

	/**
	 * 游戏结束的操作
	 */
	public void gameover() {
		System.out.println(""
				+ "\n----------------"
				+ "\n    GAMEOVER"
				+ "\n----------------");
	}

	/**
	 * 游戏胜利的操作
	 */
	public void gamewin() {
		System.out.println(""
				+ "\n----------------"
				+ "\n    YOU WIN !"
				+ "\n----------------");
	}

	/**
	 * 控制台输出当前状态
	 * @param isDebug 是否开启调试 (同时打印完全翻开的表)
	 */
	public void print(boolean isDebug) {
		// 顶部表头
		System.out.print("\nMS |");
		for (int i = 0; i < core.grids[0].length; i++) {
			System.out.printf("%2d", i);
		}
		// 调试模式副表表头
		if (isDebug) {
			System.out.print("\tMS |");
			for (int i = 0; i < core.grids[0].length; i++) {
				System.out.printf("%2d", i);
			}
		}
		
		// 分割线
		System.out.print("\n---+");
		for (int i = 0; i < core.grids[0].length; i++) {
			System.out.print("--");
		}
		// 调试模式副表分割线
		if (isDebug) {
			System.out.print("\t---+");
			for (int i = 0; i < core.grids[0].length; i++) {
				System.out.print("--");
			}
		}
		System.out.println();
		
		// 表内容
		for (int row = 0; row < core.grids.length; row++) {
			System.out.printf("%2d |", row); // 左侧表头
			for (int col = 0; col < core.grids[row].length; col++) {
				if (core.isGameover && core.grids[row][col] == Core.MINE) {
					System.out.print(" " + Core.MINE);
				} else if (core.status[row][col] == Core.STAT_OPEN) {
					if (core.grids[row][col] == '0') {
						System.out.print(" " + EMPTY);
					} else {
						System.out.print(" " + core.grids[row][col]);
					}
				} else {
					System.out.print(" " + COVERED);
				}
			}
			
			// 调试模式副表内容
			if (isDebug) {
				System.out.printf("\t%2d |", row);
				for (int col = 0; col < core.grids[row].length; col++) {
					System.out.print(" " + core.grids[row][col]);
				}
			}
			System.out.println();
		}
	}

}
