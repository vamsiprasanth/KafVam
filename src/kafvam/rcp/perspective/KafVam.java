package kafvam.rcp.perspective;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import kafvam.rcp.view.BrokerDetails;
import kafvam.rcp.view.ControlPanel;
import kafvam.rcp.view.Topic;
import kafvam.rcp.view.TopicMessages;
import kafvam.rcp.view.TopicMetrics;

public class KafVam implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		List<String> viewIds = new ArrayList<>();
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		IFolderLayout brokerDetails = layout.createFolder("BrokerDetails", IPageLayout.LEFT, 0.25f, editorArea);
		brokerDetails.addPlaceholder(BrokerDetails.ID + ":*");
		brokerDetails.addView(BrokerDetails.ID);
		viewIds.add(BrokerDetails.ID);
		
		
		layout.addStandaloneView(TopicMetrics.ID, false, IPageLayout.RIGHT, 0.25f, "BrokerDetails");
		viewIds.add(TopicMetrics.ID);

		IFolderLayout topic = layout.createFolder("Topic", IPageLayout.BOTTOM, 0.25f, "BrokerDetails");
		topic.addPlaceholder(Topic.ID + ":*");
		topic.addView(Topic.ID);
		viewIds.add(Topic.ID);

		
		IFolderLayout consoleLayout = layout.createFolder("LogConsole", IPageLayout.BOTTOM, 0.55f, TopicMetrics.ID);
		consoleLayout.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW + ":*");
		consoleLayout.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		IFolderLayout topicDetails = layout.createFolder("TopicDetails", IPageLayout.RIGHT, 0.35f, TopicMetrics.ID);
		topicDetails.addPlaceholder(TopicMessages.ID + ":*");
		topicDetails.addView(TopicMessages.ID);
		viewIds.add(TopicMessages.ID);
		
		IFolderLayout controlpanel = layout.createFolder("ControlPanel", IPageLayout.RIGHT, 0.75f, "TopicDetails");
		controlpanel.addPlaceholder(ControlPanel.ID + ":*");
		controlpanel.addView(ControlPanel.ID);
		viewIds.add(ControlPanel.ID);
		
		viewIds.forEach(id -> {
			layout.getViewLayout(id).setCloseable(false);
		});

	}

}
