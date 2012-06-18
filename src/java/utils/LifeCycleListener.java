import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

public class LifeCycleListener implements PhaseListener {

      /**
    *
    */
   private static final long serialVersionUID = 1L;

   public PhaseId getPhaseId() {
           return PhaseId.ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event) {
        FacesContext facesCtx = event.getFacesContext();
        ExternalContext extCtx = facesCtx .getExternalContext();
        HttpSession session = (HttpSession)extCtx .getSession(false);
        boolean newSession = (session == null) || (session.isNew());
        boolean postback = !extCtx.getRequestParameterMap().isEmpty();
        boolean timedout = postback && newSession;
        if(timedout) {
           Application app = facesCtx.getApplication();
           ViewHandler viewHandler = app.getViewHandler();
           facesCtx.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"Session Time Out !", ""));
           UIViewRoot view = viewHandler.createView( facesCtx, "/error.xhtml");
           facesCtx.setViewRoot(view);
           facesCtx.renderResponse();
           try {
              viewHandler.renderView(facesCtx, view);
              facesCtx.responseComplete();
           } catch(Throwable t) {
              throw new FacesException( "Session timed out", t);
           }
           
        }
    }

    public void afterPhase(PhaseEvent event) {
    }


}