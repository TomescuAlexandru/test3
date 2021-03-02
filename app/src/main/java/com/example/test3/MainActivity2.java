package com.example.test3;
;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.*;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.*;

import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TranslationController;

import java.util.EnumSet;
import java.util.Objects;


public class MainActivity2 extends AppCompatActivity implements Scene.OnUpdateListener, View.OnClickListener {
    protected Anchor currentAnchor = null;
    protected AnchorNode currentAnchorNode;
    protected TransformableNode[] nodeArrayList = new TransformableNode[100];
    CustomAr customAr;
    protected TextView tvDistance;
    private ModelRenderable chairRen;
    private ModelRenderable drawerRen;
    private ModelRenderable tableRen;
    private ModelRenderable simple_tableRen;
    ImageView drawer, chair, table, simpleT;
    View[] arrayView;
    int selected = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customAr = (CustomAr) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        //View
        chair = findViewById(R.id.chair);
        drawer = findViewById(R.id.drawer);
        table = findViewById(R.id.w);
        simpleT = findViewById(R.id.st);
        setArrayView();
        setClickListener();
        setupModel();
        customAr.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(customAr.getArSceneView().getScene());
           // ModelRenderable.builder().setSource(this,  modelInteractor.getModelSfbRes()).build().thenAccept(modelRenderable -> createModel(modelRenderable, anchorNode, selected, modelInteractor.getPlaneType()));
            createModel(anchorNode,selected,plane.getType());
            clearAnchor();

        });
    }

    private void setupModel() {
        ModelRenderable.builder().setSource(this, R.raw.drawer).build().thenAccept(renderable -> drawerRen = renderable).exceptionally(
                throwable -> {

                    Toast.makeText(this, "Nu s-a putut creea obiectul DOG", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
        ModelRenderable.builder().setSource(this, R.raw.chair).build().thenAccept(renderable -> chairRen = renderable).exceptionally(
                throwable -> {
                    Toast.makeText(this, "Nu s-a putut creea obiectul cat", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
        ModelRenderable.builder().setSource(this, R.raw.w).build().thenAccept(renderable -> tableRen = renderable).exceptionally(
                throwable -> {
                    Toast.makeText(this, "Nu s-a putut creea obiectul table", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );
        ModelRenderable.builder().setSource(this, R.raw.st).build().thenAccept(renderable -> simple_tableRen = renderable).exceptionally(
                throwable -> {
                    Toast.makeText(this, "Nu s-a putut creea obiectul simpleT", Toast.LENGTH_SHORT).show();
                    return null;
                }
        );


    }

    private void setClickListener() {
        for (View view : arrayView) {
            view.setOnClickListener(this);
        }

    }

    private void setArrayView() {
        arrayView = new View[]{
                chair, drawer, table, simpleT
        };
    }

    private void createModel(AnchorNode anchorNode , int selected, Plane.Type planeType) {
        if (selected == 1) {
            Vector3 size = ((Box) Objects.requireNonNull(drawerRen.getCollisionShape())).getSize();
            TransformableNode dog = new TransformableNode(customAr.getTransformationSystem());
            dog.setParent(anchorNode);
            if (planeType == Plane.Type.HORIZONTAL_DOWNWARD_FACING) {
                Node down = new Node();
                down.setParent(dog);
                down.setLocalPosition(new Vector3(0, size.y, 0));
                down.setLocalRotation(new Quaternion(0, 0, 1, 0));
                down.setRenderable(drawerRen);
            }else if(planeType == Plane.Type.VERTICAL){
                Node downward = new Node();
                downward.setParent(dog);

                downward.setRenderable(drawerRen);
            }
            else {
                dog.setRenderable(drawerRen);
            }
            TranslationController controller = dog.getTranslationController();
            controller.setAllowedPlaneTypes(EnumSet.of(planeType));
            dog.select();

            /*if (nodeArrayList[i] == null) {
                nodeArrayList[i] = dog;
                i = i + 1;
                customAr.getArSceneView().getScene().addOnUpdateListener(this);
            }*/
        } else if (selected == 2) {
            TransformableNode cat = new TransformableNode(customAr.getTransformationSystem());
            cat.setRenderable(chairRen);
            cat.setParent(anchorNode);
            cat.select();

            /*if (nodeArrayList[i] == null) {
                nodeArrayList[i] = dog;
                i = i + 1;
                customAr.getArSceneView().getScene().addOnUpdateListener(this);
            }*/
        } else if (selected == 3) {
            TransformableNode table = new TransformableNode(customAr.getTransformationSystem());
            table.setRenderable(tableRen);
            table.setParent(anchorNode);
            table.select();

            /*if (nodeArrayList[i] == null) {
                nodeArrayList[i] = dog;
                i = i + 1;
                customAr.getArSceneView().getScene().addOnUpdateListener(this);
            }*/
        } else if (selected == 4) {
            TransformableNode simT = new TransformableNode(customAr.getTransformationSystem());
            simT.setRenderable(simple_tableRen);
            simT.setParent(anchorNode);
            simT.select();

            /*if (nodeArrayList[i] == null) {
                nodeArrayList[i] = dog;
                i = i + 1;
                customAr.getArSceneView().getScene().addOnUpdateListener(this);
            }*/
        }
    }


    @Override
    public void onUpdate(FrameTime frameTime) {


        for (int i = 0; i < nodeArrayList.length; i++) {
            for (int j = i + 1; j < nodeArrayList.length; j++) {
                if (nodeArrayList[i] != null && nodeArrayList[j] != null) {
                    Vector3 objectPoseA = nodeArrayList[i].getWorldPosition();
                    Vector3 objectPoseB = nodeArrayList[j].getWorldPosition();

                    float dx = objectPoseB.x - objectPoseA.x;
                    float dy = objectPoseB.y - objectPoseA.y;
                    float dz = objectPoseB.z - objectPoseA.z;
                    float distanta = (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 100;
                    tvDistance.setText("Distanta dintre obiecte: " + String.format("%.2f", distanta) + " cm");
                }
            }
        }
    }


    private void clearAnchor() {
        currentAnchor = null;


        if (currentAnchorNode != null) {
            customAr.getArSceneView().getScene().removeChild(currentAnchorNode);
            Objects.requireNonNull(currentAnchorNode.getAnchor()).detach();
            currentAnchorNode.setParent(null);
            currentAnchorNode = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.drawer) {
            selected = 1;
            // setBackgroud(v.getId());
        } else if (v.getId() == R.id.chair) {
            selected = 2;
            //setBackgroud(v.getId());

        } else if (v.getId() == R.id.w) {
            selected = 3;
            //setBackgroud(v.getId());
        } else if (v.getId() == R.id.st) {
            selected = 4;
            //setBackgroud(v.getId());
        }


    }
}