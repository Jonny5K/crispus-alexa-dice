package eu.crispus;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author Johannes Kraus
 * @since 14.02.2017
 */
public class DiceSpeechlet implements Speechlet {

    private static final Logger log = LoggerFactory.getLogger(DiceSpeechlet.class);

    private static final String SLOT_NUMBER_OF_SIDES = "sides";
    private static final String SESSION_NUMBER_OF_SIDES = "NUMBER_OF_SIDES";
    private static final String INTENT_CHOOSE = "chooseDice";
    private static final String INTENT_ROLEDICE = "rollDice";
    private static final String INTENT_AMAZON_HELP = "AMAZON.HelpIntent";
    private static final String INTENT_AMAZON_STOP = "AMAZON.StopIntent";

    public void onSessionStarted(SessionStartedRequest sessionStartedRequest, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId {}, sessionId {}", sessionStartedRequest.getRequestId(), session.getSessionId());

        session.setAttribute(SESSION_NUMBER_OF_SIDES, 6);
    }

    public SpeechletResponse onLaunch(LaunchRequest launchRequest, Session session) throws SpeechletException {
        log.info("onLaunch requestId {}, sessionId {}", launchRequest.getRequestId(), session.getSessionId());

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Willkommen beim Würfel.");
        return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
    }

    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        log.info("onIntentrequestId {}, sessionId {}", intentRequest.getRequestId(), session.getSessionId());

        String intentName = intentRequest.getIntent().getName();
        if (INTENT_CHOOSE.equals(intentName)) {
            return handleChooseDice(intentRequest.getIntent(), session);
        } else if (INTENT_ROLEDICE.equals(intentName)) {
            return handleRoleDice(session);
        } else {
            if (INTENT_AMAZON_HELP.equals(intentName)) {
                return handleHelpIntent();
            } else if (INTENT_AMAZON_STOP.equals(intentName)) {
                return handleStopIntent();
            } else {
                throw new SpeechletException("Invalid Intent");
            }
        }
    }

    private SpeechletResponse handleRoleDice(Session session) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();

        int sides = (Integer) session.getAttribute(SESSION_NUMBER_OF_SIDES);
        int randomNumber = new Random().nextInt(sides) + 1;

        speech.setText("die gewürfelte zahl lautet: " + randomNumber);

        return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
    }

    private SpeechletResponse handleStopIntent() {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("auf wiedersehen");
        return SpeechletResponse.newTellResponse(speech);
    }

    private SpeechletResponse handleHelpIntent() {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("bestimme wieviele seiten dein würfel hat oder würfel");
        return SpeechletResponse.newTellResponse(speech);
    }

    private SpeechletResponse handleChooseDice(Intent intent, Session session) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();

        Slot slot = intent.getSlot(SLOT_NUMBER_OF_SIDES);
        if (slot == null || StringUtils.isBlank(slot.getValue())) {
            speech.setText("ich habe nicht verstanden wieviele seiten der würfel hat.");
        } else {
            Integer numberIfSides = Integer.valueOf(slot.getValue());

            session.setAttribute(SESSION_NUMBER_OF_SIDES, numberIfSides);

            speech.setText("ich benutze jetzt einen " + numberIfSides + " seitigen würfel.");
        }

        return SpeechletResponse.newAskResponse(speech, createRepromptSpeech());
    }

    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
        log.info("onSessionEnded requestId {}, sessionId {}", sessionEndedRequest.getRequestId(), session.getSessionId());
    }

    private Reprompt createRepromptSpeech() {
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("ich habe dich nicht verstanden");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptSpeech);

        return reprompt;
    }
}
