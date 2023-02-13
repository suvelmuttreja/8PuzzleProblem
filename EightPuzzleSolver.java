import java.util.*;
import java.io.*;

class EightPuzzleSolver {
	private final int[][] goal = {{7, 8, 1}, {6, 0, 2}, {5, 4, 3}};
    private int states = 0;
    int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    private class Node {
        int[][] state;
        int x;
        int y;
        int depth;
        int cost = Integer.MAX_VALUE;
        Node parent;

        Node(int[][] state, int x, int y, int depth, Node parent) {
            this.state = state;
            this.x = x;
            this.y = y;
            this.depth = depth;
            this.parent = parent;
        }
    }

    private boolean isGoal(int[][] state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != goal[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

	public int[] getCoords(int[][] grid, int tile) {
		int[] position = new int[2];

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (grid[x][y] == tile) {
					position[0] = x;
					position[1] = y;
					break;
				}
			}
		}
		return position;
	}

	public boolean noLoop(Node node, Node parent) {
		if(parent == null) return true;
		int count = 0;
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(node.state[i][j] == parent.state[i][j]) count++;
			}
		}
		if(count == 9) return false;
		if(parent.parent == null) return true;
		else return noLoop(node, parent.parent);
	}
	
	public void printGrid(int[][] grid) {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				if(grid[i][j] == 0) System.out.print("* ");
				else System.out.print(grid[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public void printPath(Node node) {
		if (node == null) return;

		printPath(node.parent);
		printGrid(node.state);
		System.out.println();
	}
	
	public void printResult(Node node) {
		System.out.println("\nSolution found at depth " + node.depth + "!\n");
        System.out.println("States in the path from start to goal state:");
		printPath(node);
		System.out.println("Number of moves = " + node.depth);
        System.out.println("Number of states enqueued = " + states);
	}

    public int calcMisCost(int[][] initial, int[][] goal) {
		int count = 0;
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (initial[i][j] != goal[i][j]) {
					count++;
				}
			}
		}
		return count;
	}
	
	public int calcManCost(int[][] initial, int[][] goal) {
		int distance;
		int total = 0;

		for (int i = 0; i < 9; i++) {
			int[] current = getCoords(initial, i);
			int[] target = getCoords(goal, i);
			distance = Math.abs(current[0] - target[0]) + Math.abs(current[1] - target[1]);
			total += distance;
		}
		return total;
	}
	
	public boolean aStar(Node root, boolean heur1) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(1000, (a, b) -> (a.cost + a.depth) - (b.cost + b.depth));
		if(heur1) root.cost = calcMisCost(root.state, goal);
		else root.cost = calcManCost(root.state, goal);
		pq.add(root);
		
		while (!pq.isEmpty()) {
			Node curr = pq.poll();
			if (curr.cost == 0 && isGoal(curr.state)) {
				printResult(curr);
				return true;
			}
			
			for (int[]move: moves) {
				int a = curr.x + move[0];
	            int b = curr.y + move[1];
	            if (a >= 0 && a < 3 && b >= 0 && b < 3 && curr.depth <= 10) {
	                int[][] newState = new int[3][3];
	                states++;
	                for (int i = 0; i < 3; i++) {
	                    for (int j = 0; j < 3; j++) {
	                        newState[i][j] = curr.state[i][j];
	                    }
	                }
	                newState[curr.x][curr.y] = newState[a][b];
	                newState[a][b] = 0;
	            	Node child = new Node(newState, a,b, curr.depth + 1, curr);
	            	
	            	if(heur1) child.cost = calcMisCost(child.state, goal);
	            	else child.cost = calcManCost(child.state, goal);
	            	
	            	if(noLoop(child, child.parent)) pq.add(child);
	            }
	        }
		}
		return false;
	}
    
    private boolean dfs(Node node, int maxDepth) {
        if (node.depth > maxDepth) return false;
        
        if (isGoal(node.state)) {
        	printResult(node);
            return true;
        }

        int x = node.x;
        int y = node.y;
        int[][] state = node.state;
        if (noLoop(node, node.parent)) {
	        for (int[] move : moves) {
	            int a = x + move[0];
	            int b = y + move[1];
	            if (a >= 0 && a < 3 && b >= 0 && b < 3) {
	                int[][] newState = new int[3][3];
	                states++;
	                for (int i = 0; i < 3; i++) {
	                    for (int j = 0; j < 3; j++) {
	                        newState[i][j] = state[i][j];
	                    }
	                }
	                newState[x][y] = newState[a][b];
	                newState[a][b] = 0;
	                if (dfs(new Node(newState, a, b, node.depth + 1, node), maxDepth)) {
	                    return true;
	                }
	            }
	        }
        }
        return false;
    }

    public boolean solve(int[][] state, int depth, boolean doaStar, boolean heur1) {
    	states = 0;
        int[] coords = getCoords(state, 0);
        Node start = new Node(state, coords[0], coords[1], 0, null);
        if ((!doaStar && dfs(start, depth)) || (doaStar && aStar(start, heur1))) {
        	return true;
        } else {
            System.out.println("\nNo solution found in the given depth limit of " + depth + ".");
        }
        return false;
    }

    public static void main(String[] args) throws FileNotFoundException {
		String method = args[0];

		String initial = "";
		try {
		File myObj = new File(args[1]);
      	Scanner myReader = new Scanner(myObj);
		  initial = myReader.nextLine();
		} catch (FileNotFoundException ex){throw new FileNotFoundException("Missing file");}
		
        initial = initial.replace('*', '0').trim();
        int[][] state = new int[3][3];
		System.out.println("\nInput: ");
        for(int i = 0; i < 3; i++) {
        	for(int j = 0; j < 3; j++) {
				String charint= String.valueOf(initial.charAt(2 * (i * 3 + j)));
				state[i][j] = Integer.valueOf(charint);
				if(state[i][j] == 0) System.out.print("* ");
				else System.out.print(state[i][j] + " ");
        	}
			System.out.println();
        }

        for(int i = 0; i <= 10; i++) {
        	if(!method.equals("ids")) i = 10;
        	EightPuzzleSolver puzzle = new EightPuzzleSolver();
        	boolean solved = puzzle.solve(state, i, method.contains("astar"), method.charAt(method.length() - 1) == '1');
        	if(solved) break;
        }
		System.out.println();
    }
}
