import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static int q, n, m, p, k, s, l;
	public static long totalScore = 0;
    
	static class Rabbit implements Comparable<Rabbit> {
		@Override
		public String toString() {
			return "Rabbit [id=" + id + ", d=" + d + ", count=" + count + ", r=" + (r + 1) + ", c=" + (c + 1) + ", score=" + score
					+ "]";
		}

		int id;
		int d; // distance

		int count;
		int r, c; // row, column

		int score;

		public Rabbit(int id, int d) {
			this.id = id;
			this.d = d;
			this.count = 0;
			this.r = 0;
			this.c = 0;
			this.score = 0;
		}

		public void setDistance(int ratio) {
			this.d *= ratio;
		}

		@Override
		public int compareTo(Rabbit rb) {
			if (this.count != rb.count)
				return this.count - rb.count;
			if (this.r + this.c != rb.r + rb.c)
				return (this.r + this.c) - (rb.r + rb.c);
			if (this.r != rb.r)
				return this.r - rb.r;
			if (this.c != rb.c)
				return this.c - rb.c;
			return this.id - rb.id;
		}
	}

	static class Coordinate implements Comparable<Coordinate> {
		int r;
		int c;

		public Coordinate(int r, int c) {
			this.r = r;
			this.c = c;
		}
		
		Coordinate up(int d) {
			int nr;

			nr = (r - d) % (2 * (n - 1));
			//System.out.printf("%d, %d\n", r, d);
			//System.out.println(r - d);
			//System.out.println((2 * (n - 1)));
			//System.out.println(nr);

			if(nr <= 0) 
				nr += 2 * (n - 1);
//			System.out.println(nr);
			if (nr >= n)
				nr = 2 * (n - 1) - nr;

			return new Coordinate(nr, c);
		}

		Coordinate down(int d) {
			int nr;

			nr = (r + d) % (2 * (n - 1));
			if (nr >= n)
				nr = 2 * (n - 1) - nr;

			return new Coordinate(nr, c);
		}

		Coordinate toLeft(int d) {
			int nc;

			nc = (c - d) % (2 * (m - 1));
			//if(nc < 0) System.out.println(nc);
			if (nc <= 0)
				nc += 2 * (m - 1);
			if (nc >= m)
				nc = 2 * (m - 1) - nc;
			
			return new Coordinate(r, nc);
		}

		Coordinate toRight(int d) {
			int nc;

			nc = (c + d) % (2 * (m - 1));
			if (nc >= m)
				nc = 2 * (m - 1) - nc;

			return new Coordinate(r, nc);
		}

		public int getScore() {
			return this.r + this.c + 2; //좌표가 0부터 시작
		}

		@Override
		public int compareTo(Coordinate c) {
			if (this.r + this.c != c.r + c.c)
				return (c.r + c.c) - (this.r + this.c);
			if (this.r != c.r)
				return c.r - this.r;
			return c.c - this.c;
		}
	}

    public static PriorityQueue<Rabbit> rabbitQueue = new PriorityQueue<>();
    public static PriorityQueue<Coordinate> coordinateQueue = new PriorityQueue<>();
    
//    public static Map<Integer, Integer> rabbit_map = new HashMap<>(); // rabbit.id, index(address) : 주어진 id로 토끼를 빠르게 탐색
    public static Map<Integer, Rabbit> rabbits = new HashMap<>(); // index(address), box : map을 통해 index만 알면 토끼를 빠르게 얻음.
   
	public static void main(String[] args) throws IOException {
		//System.setIn(new FileInputStream("input/rabbit"));
		Scanner sc = new Scanner(System.in);
        
        q = sc.nextInt();
		while (q-- > 0) {
            int cmd = sc.nextInt();
            switch (cmd) {
            case 100: //경주 시작 준비
            	ready(sc);
            	break;
            case 200: //경주 진행
            	run(sc);
            	break;
            case 300: //이동거리 변경
            	change(sc);
            	break;
            case 400: //최고의 토끼 선정
            	prize(sc);
				break;
			default:
				//do nothing
				break;
			}
        }
	}

	private static void ready(Scanner sc) {
        n = sc.nextInt();
        m = sc.nextInt();
        p = sc.nextInt();
        for(int i = 1; i <= p; i++) {
            int id = sc.nextInt();
            int d = sc.nextInt();
//            rabbit_map.put(id, i);
            Rabbit rb = new Rabbit(id, d);
            rabbits.put(id,rb);
            rabbitQueue.add(rb);
//            System.out.println(rb.toString());
        }
		
	}


	private static void run(Scanner sc) {
        int k = sc.nextInt();
        int s = sc.nextInt();
        
        PriorityQueue<Rabbit> scoreQueue = new PriorityQueue<>(new Comparator<Rabbit>() {
            @Override
            public int compare(Rabbit o1, Rabbit o2) {
                if(o1.r + o1.c != o2.r + o2.c) return (o2.r + o2.c) - (o1.c + o1.c);
                if(o1.c != o2.c) return o2.c - o1.c;
                if(o1.r != o2.r) return o2.r - o1.r;
                return o2.id - o1.id;
            }
        });
         
        Set<Integer> rabbitIds = new HashSet<Integer>();
//		System.out.println(k);
        
        while(k-- > 0) {
        	Rabbit player = rabbitQueue.poll();
//			System.out.println(player.toString());

        	Coordinate pLocation = new Coordinate(player.r, player.c);
        	coordinateQueue.add(pLocation.up(player.d));
        	coordinateQueue.add(pLocation.down(player.d));
        	coordinateQueue.add(pLocation.toLeft(player.d));
        	coordinateQueue.add(pLocation.toRight(player.d));
        	
        	Coordinate newLocation = coordinateQueue.poll();
        	coordinateQueue.clear();
        	
        	player.r = newLocation.r;
        	player.c = newLocation.c;
        	
        	player.score -= newLocation.getScore();
        	player.count++;
        	
        	totalScore += newLocation.getScore();
        	
//			System.out.println(player.toString());
//        	for(Rabbit rb:rabbits.values()) {
//        		System.out.println(rb.toString());
//        	}
//			System.out.println(totalScore);
//			System.out.println("");

			rabbitQueue.add(player);
//			System.out.println(player.id);
        	scoreQueue.add(player);
//			rabbitIds.add(player.id);
        }
        
//        for(Integer id : rabbitIds) scoreQueue.add(rabbits.get(id));
//        scoreQueue.poll().score += s;
//        System.out.println(rabbitIds.size());
        Rabbit winner = scoreQueue.poll();
        winner.score += s;
//        System.out.println(winner.toString());
//		System.out.println("");
	}

	private static void change(Scanner sc) {
		int pid = sc.nextInt();
		l = sc.nextInt();
//		System.out.println(pid);
//		System.out.println(l);
		
		rabbits.get(pid).setDistance(l);
	}

	private static void prize(Scanner sc) {
		long ans = 0;
		for(Rabbit rb:rabbits.values()) {
//			System.out.println(rb.toString());
			ans = Math.max(ans, rb.score + totalScore);
		}
		System.out.println(ans);
	}

}