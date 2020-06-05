package edu.hust.soict.huynv.entities;

import com.joshuacrotts.standards.StandardDraw;
import com.joshuacrotts.standards.StandardGameObject;
import com.joshuacrotts.standards.StandardID;
import com.joshuacrotts.standards.StdOps;
import edu.hust.soict.huynv.GenericSpaceShooter;
import edu.hust.soict.huynv.network.packets.PacketControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends StandardGameObject implements KeyListener {

    private GenericSpaceShooter gss;

    private short FIRE_INTERVAL = 20;

    private short interval = FIRE_INTERVAL;

    private String username;

    public int score;

    public Player(double x, double y, GenericSpaceShooter gss, String username){
        super(x, y, StandardID.Player);
        this.gss = gss;
        this.currentSprite = StdOps.loadImage("res/player.png");
        this.username = username;
        this.width = this.currentSprite.getWidth();
        this.height = this.currentSprite.getHeight();

        this.health = 500;
    }

    @Override
    public void tick() {

        if(this.health <= 0){
            GenericSpaceShooter.standardHandler.removeEntity(this);
            JOptionPane.showMessageDialog(null, "Player " + username + " died, score was: "+GenericSpaceShooter.score);
//            System.exit(0);
        }

        this.x += this.velX;
        this.y += this.velY;

        PacketControl packetControl = new PacketControl(this.username, (int) this.getX(), (int) this.getY());
        packetControl.writeData(gss.socketClient);

        this.firBulletCheck();
        this.checkCoordinates();
    }

    @Override
    public void render(Graphics2D graphics2D) {
        graphics2D.drawImage(this.currentSprite, (int) x, (int) y, null);

        StandardDraw.text("Name: "+ this.username, (int) this.x + 50, (int) this.y +10 , "", 15f, Color.BLUE);
        StandardDraw.text("Life: "+this.health, (int) this.x + 50, (int) this.y +20, "", 15f, Color.GREEN);
        StandardDraw.text("Score: "+this.score, (int) this.x + 50, (int) this.y +30, "", 15f, Color.RED);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent keyEvent){
        switch (keyEvent.getKeyCode()){
            case KeyEvent.VK_SPACE: this.fireBullet();break;
            case KeyEvent.VK_A: this.velX = -5; break;
            case KeyEvent.VK_D: this.velX = 5; break;
            case KeyEvent.VK_S: this.velY = 5; break;
            case KeyEvent.VK_W: this.velY = -5; break;
        }
    }

    public void keyReleased(KeyEvent keyEvent){
        switch (keyEvent.getKeyCode()){
            case KeyEvent.VK_A: this.velX = 0; break;
            case KeyEvent.VK_D: this.velX = 0; break;
            case KeyEvent.VK_S: this.velY = 0; break;
            case KeyEvent.VK_W: this.velY = 0; break;
        }
    }

    public void checkCoordinates(){

        if(this.x <= 0){
            this.x = 0;
        }

        if(this.y <= 0){
            this.y = 0;
        }

        if(this.x >= this.gss.getWidth() - this.width){
            this.x = this.gss.getWidth() - this.width;
        }

        if(this.y >= this.gss.getHeight() - this.height){
            this.y = this.gss.getHeight() - this.height;
        }
    }

    private void fireBullet(){
        if(this.interval < FIRE_INTERVAL){
            return;
        }else{
            this.interval = 0;
            GenericSpaceShooter.standardHandler.addEntity(new Bullet((this.x + this.width / 2), this.y, -20, this.getId()));
        }
    }

    private void firBulletCheck(){
        this.interval ++;

        if(this.interval > FIRE_INTERVAL){
            this.interval = FIRE_INTERVAL;
        }
    }

    public String getUsername() {
        return this.username;
    }
}
