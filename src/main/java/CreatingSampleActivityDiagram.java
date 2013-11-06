import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.ActivityDiagramEditor;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.IDiagramEditorFactory;
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

    private void createActivityDiagram() throws InvalidEditingException, ClassNotFoundException,
            ProjectNotFoundException, InvalidUsingException {
        ProjectAccessor projectAccessor = AstahAPI.getAstahAPI().getProjectAccessor();
        IModel project = projectAccessor.getProject();

        BasicModelEditor modelEditor = ModelEditorFactory.getBasicModelEditor();
        IPackage samplePackage = modelEditor.createPackage(project, "Sample");

        IDiagramEditorFactory diagramEditorFactory = projectAccessor.getDiagramEditorFactory();
        ActivityDiagramEditor diagramEditor = diagramEditorFactory.getActivityDiagramEditor();
        diagramEditor.createActivityDiagram(samplePackage, "SampleActivityDiagram");

        INodePresentation partition0 = diagramEditor.createPartition(null, null, "partition0", false);
        partition0.setWidth(300);
        INodePresentation partition1 = diagramEditor.createPartition(null, partition0, "partition1", false);
        partition1.setWidth(300);
        INodePresentation partition2 = diagramEditor.createPartition(null, partition1, "partition2", false);
        partition2.setWidth(300);
        INodePresentation partition01 = diagramEditor.createPartition(partition0, null, "partition01", false);
        INodePresentation partition00 = diagramEditor.createPartition(null, null, "partition00", true);
        partition00.setHeight(250);
        INodePresentation partition10 = diagramEditor.createPartition(null, partition00, "partition10", true);
        partition10.setHeight(250);

        Rectangle2D p01Rect = partition01.getRectangle();
        INodePresentation initialNode = diagramEditor.createInitialNode("InitialNode",
                new Point2D.Double(p01Rect.getCenterX() - 10, p01Rect.getMinY() + 80));

        Rectangle2D initialRect = initialNode.getRectangle();
        INodePresentation action0 = diagramEditor.createAction("Action0",
                new Point2D.Double(p01Rect.getCenterX() - 40, initialRect.getMaxY() + 50));
        Rectangle2D action0Rect = action0.getRectangle();
        INodePresentation pin0 = diagramEditor.createPin("Pin0", null, false, action0,
                new Point2D.Double(action0Rect.getMaxX(), action0Rect.getCenterY() - 5));

        diagramEditor.createFlow(initialNode, action0);

        INodePresentation object0 = diagramEditor.createObjectNode("Object0", null,
                new Point2D.Double(p01Rect.getMaxX() - 30, action0Rect.getMinY()));

        diagramEditor.createFlow(pin0, object0);

        Rectangle2D p1Rect = partition1.getRectangle();
        Point2D action0Loc = action0.getLocation();
        INodePresentation action1 = diagramEditor.createAction("Action1",
                new Point2D.Double(p1Rect.getCenterX() - 40, action0Loc.getY()));
        Rectangle2D act1Rect = action1.getRectangle();
        INodePresentation pin1 = diagramEditor.createPin("Pin1", null, true, action1,
                new Point2D.Double(act1Rect.getMinX(), act1Rect.getCenterY() - 5));

        diagramEditor.createFlow(object0, pin1);

        INodePresentation mergeNode0 = diagramEditor.createDecisionMergeNode(null,
                new Point2D.Double(act1Rect.getCenterX() - 15, act1Rect.getMaxY() + 50));

        diagramEditor.createFlow(action1, mergeNode0);

        Point2D p10Loc = partition10.getLocation();
        INodePresentation action2 = diagramEditor.createAction("Action2",
                new Point2D.Double(p1Rect.getCenterX() - 40, p10Loc.getY() + 50));

        ILinkPresentation flow2 = diagramEditor.createFlow(mergeNode0, action2);
        ((IFlow) flow2.getModel()).setGuard("To do Action2");

        Rectangle2D p2Rect = partition2.getRectangle();
        INodePresentation action3 = diagramEditor.createAction("Action3",
                new Point2D.Double(p2Rect.getCenterX() - 40, p10Loc.getY() + 50));

        ILinkPresentation flow3 = diagramEditor.createFlow(mergeNode0, action3);
        ((IFlow) flow3.getModel()).setGuard("To do Action3");

        INodePresentation finaleNode = diagramEditor.createFinalNode("FinalNode",
                new Point2D.Double(p2Rect.getCenterX() - 10, p2Rect.getMaxY() - 50));

        diagramEditor.createFlow(action3, finaleNode);
    }
}
