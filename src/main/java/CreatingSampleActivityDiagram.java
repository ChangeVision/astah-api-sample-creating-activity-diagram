import java.awt.geom.Point2D;
import java.io.IOException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ActivityDiagramEditor;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;
import com.change_vision.jude.api.inf.exception.InvalidUsingException;
import com.change_vision.jude.api.inf.exception.LicenseNotFoundException;
import com.change_vision.jude.api.inf.exception.ProjectLockedException;
import com.change_vision.jude.api.inf.exception.ProjectNotFoundException;
import com.change_vision.jude.api.inf.model.IFlow;
import com.change_vision.jude.api.inf.model.IModel;
import com.change_vision.jude.api.inf.model.IPackage;
import com.change_vision.jude.api.inf.presentation.ILinkPresentation;
import com.change_vision.jude.api.inf.presentation.INodePresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class CreatingSampleActivityDiagram {
    public static void main(String[] args) {
        try {
            new CreatingSampleActivityDiagram().run("SampleAcitivityDiagram.asta");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String projectName) throws ClassNotFoundException, LicenseNotFoundException,
    ProjectNotFoundException, IOException, ProjectLockedException {
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        try {
            projectAccessor.create(projectName);
            TransactionManager.beginTransaction();

            createActivityDiagram();

            TransactionManager.endTransaction();
            projectAccessor.save();

            System.out.println("Done createing the sample activity diagram.");
        } catch (InvalidEditingException e) {
            e.printStackTrace();
        } catch (InvalidUsingException e) {
            e.printStackTrace();
        } finally {
            TransactionManager.abortTransaction();
            projectAccessor.close();
        }
    }

    private void createActivityDiagram() throws InvalidEditingException, ClassNotFoundException, ProjectNotFoundException, InvalidUsingException {
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        IModel project = projectAccessor.getProject();

        BasicModelEditor modelEditor = ModelEditorFactory.getBasicModelEditor();
        IPackage samplePackage = modelEditor.createPackage(project, "Sample");

        ActivityDiagramEditor diagramEditor = projectAccessor.getDiagramEditorFactory().getActivityDiagramEditor();
        diagramEditor.createActivityDiagram(samplePackage, "SampleActivityDiagram");

        INodePresentation partition0 = diagramEditor.createPartition(null, null, "Partion0", false);
        partition0.setWidth(300);
        INodePresentation partition1 = diagramEditor.createPartition(null, partition0, "Partion1", false);
        partition1.setWidth(300);
        INodePresentation partition2 = diagramEditor.createPartition(null, partition1, "Partion2", false);
        partition2.setWidth(300);
        INodePresentation partition01 = diagramEditor.createPartition(partition0, null, "Partion01", false);
        INodePresentation partition00 = diagramEditor.createPartition(null, null, "Partion00", true);
        partition00.setHeight(250);
        INodePresentation partition10 = diagramEditor.createPartition(null, partition00, "Partion10", true);
        partition10.setHeight(250);

        INodePresentation initialNode = diagramEditor.createInitialNode("InitialNode",
                new Point2D.Double(partition01.getRectangle().getCenterX() - 10, partition01.getRectangle().getMinY() + 80)); 

        INodePresentation action0 = diagramEditor.createAction("Action0",
                new Point2D.Double(partition01.getRectangle().getCenterX() - 40, initialNode.getRectangle().getMaxY() + 50)); 
        INodePresentation pin0 = diagramEditor.createPin("Pin0", null, false, action0,
                new Point2D.Double(action0.getRectangle().getMaxX(), action0.getRectangle().getCenterY() - 5));

        diagramEditor.createFlow(initialNode, action0);

        INodePresentation object0 = diagramEditor.createObjectNode("Object0", null,
                new Point2D.Double(partition01.getRectangle().getMaxX() - 30, action0.getRectangle().getMinY()));

        diagramEditor.createFlow(pin0, object0);

        INodePresentation action1 = diagramEditor.createAction("Action1",
                new Point2D.Double(partition1.getRectangle().getCenterX() - 40, action0.getLocation().getY())); 
        INodePresentation pin1 = diagramEditor.createPin("Pin1", null, true, action1,
                new Point2D.Double(action1.getRectangle().getMinX(), action1.getRectangle().getCenterY() - 5));

        diagramEditor.createFlow(object0, pin1);

        INodePresentation mergeNode0 = diagramEditor.createDecisionMergeNode(null,
                new Point2D.Double(action1.getRectangle().getCenterX() - 15, action1.getRectangle().getMaxY() + 50));

        diagramEditor.createFlow(action1, mergeNode0);

        INodePresentation action2 = diagramEditor.createAction("Action2",
                new Point2D.Double(partition1.getRectangle().getCenterX() - 40, partition10.getLocation().getY() + 50));

        ILinkPresentation flow2 = diagramEditor.createFlow(mergeNode0, action2);
        ((IFlow)flow2.getModel()).setGuard("To do Action2");

        INodePresentation action3 = diagramEditor.createAction("Action3",
                new Point2D.Double(partition2.getRectangle().getCenterX() - 40, partition10.getLocation().getY() + 50));

        ILinkPresentation flow3 = diagramEditor.createFlow(mergeNode0, action3);
        ((IFlow)flow3.getModel()).setGuard("To do Action3");

        INodePresentation finaleNode = diagramEditor.createFinalNode("FinalNode",
                new Point2D.Double(partition2.getRectangle().getCenterX() - 10, partition2.getRectangle().getMaxY() - 50)); 

        diagramEditor.createFlow(action3, finaleNode);
    }
}
