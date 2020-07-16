

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
    //deklarasi variabel yang digunakan

    public static FlappyBird flappyBird;

    public final int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public JFrame jframe;

    public Rectangle bird;
    public Rectangle enemy;

    public String birdPath = "assets\\char1.png";
    public String enemyPath = "assets\\enemy1.png";

    public ArrayList<Rectangle> columns;

    public int enemyApp;

    public int ticks, yMotion, score;

    public boolean gameOver, started, pause = false, newGame;

    //variabel untuk gambar gambar yang digunakan
    public BufferedImage birdImage;
    public BufferedImage enemyImage;
    public BufferedImage grassImage;
    public BufferedImage pipeupImage;
    public BufferedImage pipedownImage;
    public BufferedImage pipeImage;
    public BufferedImage rightArrow;
    public BufferedImage leftArrow;


    public Random rand;

    public int highScore = -1;

    public int grass;

    public int enemyIterator = 1;


    //variabel state
    public enum STATE{
        MENU,GAME
    }

    public STATE state = STATE.MENU;

    public int iChar = 1;
    public int iEnemy = 1;

    public FlappyBird()
    {
        //mendeklarasikan inormasi awal
        jframe = new JFrame();
        Timer timer = new Timer(20, this);//timer

        renderer = new Renderer();
        rand = new Random();

        try {
            //mendeklarasikan gambar pertama
            birdImage = ImageIO.read(new File(birdPath));
            enemyImage = ImageIO.read(new File(enemyPath));
            grassImage = ImageIO.read(new File("assets\\grass.png"));
            pipedownImage = ImageIO.read(new File("assets\\pipedown.png"));
            pipeupImage = ImageIO.read(new File("assets\\pipeup.png"));
            pipeImage = ImageIO.read(new File("assets\\pipe.png"));
            rightArrow = ImageIO.read(new File("assets\\rightArrow.png"));
            leftArrow = ImageIO.read(new File("assets\\leftArrow.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }



        jframe.add(renderer);
        jframe.setTitle("Flappy Bird");
        jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        //informasi awal burung dan musuhnya
        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        enemy = new Rectangle(WIDTH,HEIGHT-100-rand.nextInt(600),40,40);


         columns = new ArrayList<Rectangle>();

        //kolom rintangan awal
        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);

        timer.start();
    }


    //menambahkann column ke arraylist column
    public void addColumn(boolean start)
    {
        int space = 300;
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start)
        {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));//column bawah
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));//column atas
        }
        else //supaya tidak menunggu column berikutnya lihat x nya
        {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    public void paintColumn(Graphics g, Rectangle column)
    {
        //menggambar columnya supaya berwujud
        if(column.y==0){//jika column atas
            g.drawImage(pipeupImage,column.x,column.y,column.width,column.height,null);
        }else{//jika column bawah
            g.drawImage(pipedownImage,column.x,column.y,column.width,column.height,null);
        }

        g.drawImage(grassImage,column.x-900,HEIGHT-220,900,120, null);//ground
    }


    //method jump
    public void jump()
    {
        if (gameOver || !started)
        {
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);

            columns.clear();
            yMotion = 0;
            score = 0;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);


            gameOver = false;
        }

        if (!started)
        {
            started = true;
        }
        else if (!gameOver)
        {
            if (yMotion > 0)
            {
                yMotion = 0;
            }

            yMotion -= 10;
        }
    }

    @Override

    //hal hal yang dilakukan ketika loop
    public void actionPerformed(ActionEvent e)
    {

        if(state==STATE.GAME){//jika sedang game

            if(newGame){
                bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
                //enemy = new Rectangle(0,0,0,0);
                columns.clear();
                yMotion = 0;
                score = 0;

                addColumn(true);
                addColumn(true);
                addColumn(true);
                addColumn(true);
            }
            int speed = 10;//kecepatan game
            int enemySpeed = speed+rand.nextInt(7);//kecepatan musuh

            ticks++;
            if(highScore == -1){
                highScore = getHighScore();
            }

            if(score<2){
                enemy.width = 0;
                enemy.height = 0;
                enemy.y = 0;
            }


            if (started)
            {
                if(pause){
                    speed=0;
                    enemySpeed=0;
                    yMotion=0;
                }
                if(gameOver){
                    speed = 0;
                    enemyIterator=0;
                    if(score>highScore){//pengambilan highscore
                        writeHighScore(score);
                        highScore = getHighScore();
                    }
                }
                for (int i = 0; i < columns.size(); i++)
                {
                    Rectangle column = columns.get(i);

                    column.x -= speed;
                }

                grass -= speed;

                if (ticks % 2 == 0 && yMotion < 15 && !pause)
                {
                    yMotion += 2;//gravitasi
                }

                for (int i = 0; i < columns.size(); i++)
                {
                    Rectangle column = columns.get(i);

                    if (column.x + column.width < 0)
                    {
                        columns.remove(column);


                        addColumn(false);

                    }
                }

                bird.y += yMotion;//gravitasi burung

                enemy.x-=enemySpeed;//kecepatan musuh

                if(score==3*enemyIterator){//kemunculan musuh
                    enemy = new Rectangle(WIDTH,HEIGHT-100-rand.nextInt(500),40,40);
                    enemyIterator++;
                }


                for (Rectangle column : columns)
                {
                    if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - speed && bird.x + bird.width / 2 < column.x + column.width / 2 + speed)
                    {
                        score++;
                    }

                    if (column.intersects(bird))//jika burung menabrak column
                    {
                        gameOver = true;

                        if (bird.x <= column.x)
                        {
                            bird.x = column.x - bird.width;

                        }
                        else
                        {
                            if (column.y != 0)
                            {
                                bird.y = column.y - bird.height;
                            }
                            else if (bird.y < column.height)
                            {
                                bird.y = column.height;
                            }
                        }
                    }
                }
                if(bird.intersects(enemy)){//jika burung menabrak musuh
                    gameOver = true;//gameover
                    bird = new Rectangle(0,0,0,0);
                }

                if (bird.y > HEIGHT - 120 || bird.y < 0)
                {
                    gameOver = true;
                }

                if (bird.y + yMotion >= HEIGHT - 120)
                {
                    bird.y = HEIGHT - 120 - bird.height;
                    gameOver = true;
                }

            }
        }else if(state==STATE.MENU){//jika sedang menu

            int speed = 3;//kecepatan pada menu

            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);//kolom kolom pada menu

                column.x -= speed;
            }

            grass -= speed;

            for (int i = 0; i < columns.size(); i++)
            {
                Rectangle column = columns.get(i);

                if (column.x + column.width < 0)
                {
                    columns.remove(column);


                    addColumn(false);

                }
            }
        }

        renderer.repaint();
    }

    //komponen-komponen yang ada di menu
    public Rectangle ExitButton = new Rectangle(300, HEIGHT / 2-30,150,50);
    public Rectangle PlayButton = new Rectangle(300, 220,150,50);
    public Rectangle charBox = new Rectangle(350, 320,50,50);
    public Rectangle charRight = new Rectangle(400, 330,30,30);
    public Rectangle charLeft = new Rectangle(320, 330,30,30);
    public Rectangle enemyBox = new Rectangle(350, 420,50,50);
    public Rectangle enemyRight = new Rectangle(400, 430,30,30);
    public Rectangle enemyLeft = new Rectangle(320, 430,30,30);

    public Font fnt01 = new Font("Arial", 1, 100);
    public Font fnt02 = new Font("Arial", 1, 50);

    public void repaint(Graphics g)
    {

        Graphics2D g2d = (Graphics2D) g;

        if(state==STATE.GAME){//apa-apapun yang digambarkan di game

            //beckground (langit)
            g.setColor(Color.cyan.darker());
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.orange.darker().darker());
            g.fillRect(0, HEIGHT - 120, WIDTH, 120);//ground


            //rumput
            g.setColor(Color.green);
            g.fillRect(0, HEIGHT - 120, WIDTH, 20);
            g.drawImage(grassImage,grass,HEIGHT-220,800,120, null);

            g.setColor(Color.red);
            g.drawImage(birdImage,bird.x, bird.y, bird.width, bird.height,null);


            //foreach column yang ada
            for (Rectangle column : columns)
            {
                paintColumn(g, column);//diwujudkan
                g.drawImage(grassImage,grass,HEIGHT-220,800,120, null);
            }

            g.setColor(Color.red);
            if(score<2){
                g.fillRect(0,0,0,0);
            }else{
                //musuh
                g.drawImage(enemyImage,enemy.x,enemy.y,enemy.width,enemy.height,null);
            }

            g.setColor(Color.white);
            g.setFont(fnt01);

            if (!started)
            {
                g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
            }

            if (gameOver)
            {
                g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
                g2d.draw(ExitButton);
                g.setFont(fnt02);
                g.drawString("EXIT",ExitButton.x+20,ExitButton.y+45);
            }
            if(pause && !gameOver && started){//ketika pause dan tidak gameover dan sudah dimulai
                g.setFont(fnt01);
                g.drawString("Pause", 230, HEIGHT / 2 - 50);
                g2d.draw(ExitButton);
                g.setFont(fnt02);
                g.drawString("EXIT",ExitButton.x+20,ExitButton.y+45);
            }

            if (!gameOver && started)
            {
                g.setFont(fnt01);
                g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
            }

            g.setFont(new Font("Arial", 1, 15));
            g.drawString("ESC to pause", 10, 20);

            g.setFont(new Font("Arial", 1, 20));
            g.setColor(Color.BLACK);

            if(score>highScore){//jika score lebih besar dari highscore maka highscore mengikuti
                g.drawString("High Score : "+String.valueOf(score),WIDTH-170,HEIGHT-100);
            }else{
                g.drawString("High Score : "+String.valueOf(highScore),WIDTH-170,HEIGHT-100);
            }

        }else if(state==STATE.MENU){//apa-apa saja yang ada di menu

            //bg(langit)
            g.setColor(Color.cyan.darker());
            g.fillRect(0, 0, WIDTH, HEIGHT);


            //tanah
            g.setColor(Color.orange.darker().darker());
            g.fillRect(0, HEIGHT - 120, WIDTH, 120);


            //rumput
            g.setColor(Color.green);
            g.fillRect(0, HEIGHT - 120, WIDTH, 20);
            g.drawImage(grassImage,grass,HEIGHT-220,800,120, null);

            //supaya bergerak dibuat burung tapi tidak memiliki luas
            g.setColor(Color.red);
            g.drawImage(birdImage,bird.x, bird.y, 0, 0,null);

            //kolom hijau
            for (Rectangle column : columns)
            {
                paintColumn(g, column);
                g.drawImage(grassImage,grass,HEIGHT-220,800,120, null);
            }

            //tulisan flappy bird
            g.setColor(Color.red.darker());
            g.setFont(fnt01);
            g.drawString("Flappy Bird",120,120);


            g.setColor(Color.white);
            //panah kanan kiri karakter
            g2d.drawImage(rightArrow,charRight.x,charRight.y,charRight.width,charRight.height,null);
            g2d.drawImage(leftArrow,charLeft.x,charLeft.y,charLeft.width,charLeft.height,null);

            //panah kanan kiri musuh
            g2d.drawImage(rightArrow,enemyRight.x,enemyRight.y,enemyRight.width,enemyRight.height,null);
            g2d.drawImage(leftArrow,enemyLeft.x,enemyLeft.y,enemyLeft.width,enemyLeft.height,null);


            //tombol play
            g.setFont(fnt02);
            g.drawString("PLAY",PlayButton.x+11,PlayButton.y+45);

            //keterangan musuh/karakter
            g.setFont(new Font("Arial",1,20));
            g.drawString("You",charBox.x+3,charBox.y-2);
            g.drawString("CPU",enemyBox.x+3,enemyBox.y-2);


            //gambar musuh dan karakter yang dipilih
            g2d.drawImage(birdImage,charBox.x+15,charBox.y+15,20,20,null);
            g2d.drawImage(enemyImage,enemyBox.x+5,enemyBox.y+5,40,40,null);


        }


    }

    public static void main(String[] args)
    {
        flappyBird = new FlappyBird();
    }

    @Override
    public void mouseClicked(MouseEvent e)//mouse click event
    {
        int mx = e.getX();
        int my = e.getY();

        if(state==STATE.GAME){

            if(newGame){
                newGame=false;
            }else{
                jump();
            }

        }

    }

    @Override
    public void keyReleased(KeyEvent e)//keyboard event
    {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            jump();
            if(pause){
                pause=false;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int mx = e.getX();//ambil x dari mouse
        int my = e.getY();//ambil y dari mouse

        if(state==STATE.GAME){//jika state game

            if(pause){
                pause=false;
                if(mx>=ExitButton.x && mx<=ExitButton.x+ExitButton.width){//jikaa diklik exit pada pause
                    if(my>=ExitButton.y+30 && my<=ExitButton.y+ExitButton.height+30){
                        state=STATE.MENU;
                    }
                }
            }
            if(gameOver){
                pause=false;
                if(mx>=ExitButton.x && mx<=ExitButton.x+ExitButton.width){//jika diklik exit pada gameover
                    if(my>=ExitButton.y+30 && my<=ExitButton.y+ExitButton.height+30){
                        state=STATE.MENU;
                        gameOver=false;
                    }
                }
            }

        }else if(state==STATE.MENU){////jika state menu
            if(mx>=PlayButton.x && mx<=PlayButton.x+PlayButton.width){//jika play button diklik
                if(my>=PlayButton.y+30 && my<=PlayButton.y+PlayButton.height+30){
                    state=STATE.GAME;//state berubah menjadi game
                    started = false;
                    newGame = true;
                }
            }if((mx>=charRight.x && mx<=charRight.x+charRight.width)){//jika panah kanan karakter diklik
                if(my>=charRight.y+30 && my<=charRight.y+charRight.height+30){
                    if(iChar==1){//jika karakter yang pertama
                        birdPath = "assets\\char2.png";
                        iChar=2;
                    }else if(iChar==2){//jika karakter yang kedua
                        birdPath = "assets\\char3.png";
                        iChar=3;
                    }else if(iChar==3){//jika karakter yang ketiga
                        birdPath = "assets\\char1.png";
                        iChar=1;
                    }
                    birdImage = getImage(birdPath);
                }
            }if((mx>=charLeft.x && mx<=charLeft.x+charLeft.width)){//jika panah kiri karakter diklik
                if(my>=charLeft.y+30 && my<=charLeft.y+charLeft.height+30){
                    if(iChar==1){
                        birdPath = "assets\\char3.png";
                        iChar=3;
                    }else if(iChar==2){
                        birdPath = "assets\\char1.png";
                        iChar=1;
                    }else if(iChar==3){
                        birdPath = "assets\\char2.png";
                        iChar=2;
                    }
                    birdImage = getImage(birdPath);
                }
            }if((mx>=enemyRight.x && mx<=enemyRight.x+enemyRight.width)){//jika panah kanan musuh diklik
                if(my>=enemyRight.y+30 && my<=enemyRight.y+enemyRight.height+30){
                    if(iEnemy==1){
                        enemyPath = "assets\\enemy2.png";
                        iEnemy=2;
                    }else if(iEnemy==2){
                        enemyPath = "assets\\enemy3.png";
                        iEnemy=3;
                    }else if(iEnemy==3){
                        enemyPath = "assets\\enemy1.png";
                        iEnemy=1;
                    }
                    enemyImage = getImage(enemyPath);
                }
            }if((mx>=enemyLeft.x && mx<=enemyLeft.x+enemyLeft.width)){//jika panah kiri musuh diklik
                if(my>=enemyLeft.y+30 && my<=enemyLeft.y+enemyLeft.height+30){
                    if(iEnemy==1){
                        enemyPath = "assets\\enemy3.png";
                        iEnemy=3;
                    }else if(iEnemy==2){
                        enemyPath = "assets\\enemy1.png";
                        iEnemy=1;
                    }else if(iEnemy==3){
                        enemyPath = "assets\\enemy2.png";
                        iEnemy=2;
                    }
                    enemyImage = getImage(enemyPath);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)//event pressed untuk esc yaitu pause
        {
            pauseGame();
        }

    }

    public int getHighScore(){//membaca highscore

        FileReader filereader = null;
        BufferedReader reader = null;

        try{
            filereader = new FileReader("highscore.txt");//membukan file highscore
            reader = new BufferedReader(filereader);
            return Integer.valueOf(reader.readLine());
        }catch (IOException e){
            return 0;
        }finally{
            try {
                filereader.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public void writeHighScore(int hs){//menuliskan highscore

        FileWriter filewriter = null;

        try {
            filewriter = new FileWriter("highscore.txt");//membuat file highscore
            String writethis = String.valueOf(hs);
            filewriter.write(writethis);
            filewriter.close();
        }catch (IOException e){

        }
    }

    public void pauseGame(){//pause game
        if(pause){
            pause=false;
        }else{
            pause=true;
        }
    }

    public BufferedImage getImage(String path){//mengambil gambar untuk pemilihian karakter
        BufferedImage a = null;
        try{
            a = ImageIO.read(new File(path));

        }catch (Exception e){

        }
        return a;
    }


}