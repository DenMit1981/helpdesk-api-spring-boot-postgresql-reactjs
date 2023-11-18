import React from "react";
import {
  Button,
  InputLabel,
  FormControl,
  MenuItem,
  Select,
  TextField,
  Typography
} from "@material-ui/core";
import { Link, withRouter } from "react-router-dom";
import { CATEGORIES_OPTIONS, URGENCY_OPTIONS } from "../constants/inputsValues";
import TicketService from '../services/TicketService';
import CommentService from '../services/CommentService';
import AttachmentService from "../services/AttachmentService";

class TicketCreationPage extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      ticketId: 0,
      category: 'APPLICATION_AND_SERVICES',
      name: 'ticket',
      description: '',
      urgency: 'LOW',
      desiredResolutionDate: "2024-01-01",
      comment: '',
      selectedFile: null,
      createTicketError: [],
      isInvalidTicket: false,
      attachmentUploadError: [],
      isInvalidAttachment: false,
      fileNotFoundError: '',
      isFileSelected: true,
      isFilePresent: true,
      addFileInfo: '',
      addCommentInfo: '',
      createCommentError: [],
      isInvalidComment: false
    };

    this.handleCategoryChange = this.handleCategoryChange.bind(this);
    this.handleNameChange = this.handleNameChange.bind(this);
    this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
    this.handleUrgencyChange = this.handleUrgencyChange.bind(this);
    this.handleDesiredResolutionDate = this.handleDesiredResolutionDate.bind(this);
    this.handleCommentChange = this.handleCommentChange.bind(this);
    this.handleAttachmentChange = this.handleAttachmentChange.bind(this);
    this.handleSaveDraft = this.handleSaveDraft.bind(this);
    this.handleSubmitTicket = this.handleSubmitTicket.bind(this);
    this.addFile = this.addFile.bind(this);
    this.addComment = this.addComment.bind(this);
  }

  handleCategoryChange = (event) => {
    this.setState({
      category: event.target.value,
    });
  };

  handleNameChange = (event) => {
    this.setState({
      name: event.target.value,
    });
  };

  handleDescriptionChange = (event) => {
    this.setState({
      description: event.target.value,
    });
  };

  handleUrgencyChange = (event) => {
    this.setState({
      urgency: event.target.value,
    });
  };

  handleDesiredResolutionDate = (event) => {
    this.setState({
      desiredResolutionDate: event.target.value,
    });
  };

  handleCommentChange = (event) => {
    this.setState({
      comment: event.target.value,
    });
  };

  handleAttachmentChange = (event) => {
    this.setState({
      selectedFile: event.target.files[0]
    });
  };

  addFile = (event) => {
    const attachment = new FormData();

    attachment.append(
        "file", this.state.selectedFile,
    );

    if (this.state.selectedFile != null) {
      TicketService.getNewTicketId().then((res) => {
        const newTicketId = res.data;
        this.setState({ticketId: newTicketId});

        AttachmentService.uploadAttachment(attachment, this.state.ticketId).then(res => {
        }).catch(err => {
          if(err.response.status === 400) {
            this.setState({ isInvalidAttachment: true });
            this.setState({ attachmentUploadError: err.response.data });
            this.setState({ fileNotFoundError: '' });
            this.setState({ isFileSelected: true });
            this.setState({ addFileInfo: ''});
          }
        })
        this.setState({ addFileInfo: "File " + this.state.selectedFile.name + " was added to ticket"});
        this.setState({ attachmentUploadError: [] });
        this.setState({ isFileSelected: true });
      })
    } else {
      this.setState({ isFileSelected: false });
      this.setState({ attachmentUploadError: [] });
      this.setState({ fileNotFoundError: '' });
      this.setState({ addFileInfo: ''});
    }
  }

  addComment = (event) => {
    const comment = {
      text: this.state.comment
    }

    if (this.state.comment !== "") {
      TicketService.getNewTicketId().then((res) => {
        const newTicketId = res.data;
        this.setState({ticketId: newTicketId});

        CommentService.createComment(comment, this.state.ticketId).then(res => {
        }).catch(err => {
          if(err.response.status === 400) {
            this.setState({ isInvalidComment: true });
            this.setState({ createCommentError: err.response.data })
          } else {
            this.setState({ isInvalidComment: false });
            this.setState({ addCommentInfo: "Comment has been added to ticket" });
            this.setState({ createCommentError: [] });
          }
        })
      })
    }
  }

  handleSaveDraft = () => {
    this.handleSubmitTicket("Draft");
  }

  handleSubmitTicket = (e) => {
    const ticket = {
      category: this.state.category,
      name: this.state.name,
      description: this.state.description,
      urgency: this.state.urgency,
      desiredResolutionDate: this.state.desiredResolutionDate,
      status: this.state.status,
    };

    e === "Draft" ?
      TicketService.createTicketAsDraft(ticket).then(res => {
        this.setState({ createTicketError: [] })
        this.addFile();
        this.addComment();
        this.props.history.push(`/tickets`);
      }).catch(err => {
        if (err.response.status === 400) {
          this.setState({ isInvalidTicket: true });
          this.setState({ createTicketError: err.response.data })
        }
      }) :
      TicketService.createTicket(ticket).then(res => {
        this.setState({ createTicketError: [] })
        this.addFile();
        this.addComment();
        this.props.history.push(`/tickets`);
      }).catch(err => {
        if (err.response.status === 400) {
          this.setState({ isInvalidTicket: true });
          this.setState({ createTicketError: err.response.data })
        }
      })
  }

  render() {
    const { name, category, comment, description, desiredResolutionDate, urgency, createTicketError, isInvalidTicket,
            attachmentUploadError, isInvalidAttachment, fileNotFoundError, isFileSelected, isFilePresent, addFileInfo,
            addCommentInfo, createCommentError, isInvalidComment } = this.state;
    const { handleCategoryChange, handleNameChange, handleDescriptionChange, handleUrgencyChange,
            handleDesiredResolutionDate,handleCommentChange,handleAttachmentChange,handleSaveDraft,
            handleSubmitTicket, addFile, addComment } = this;

    return (
      <div className="ticket-creation-form-container" >
        <header className="ticket-creation-form-container__navigation-container">
          <Button component={Link} to="/tickets" variant="contained">
            Ticket List
          </Button>
        </header>
        <div className="ticket-creation-form-container__title">
          <Typography display="block" variant="h3">
            Create new ticket
          </Typography>
        </div><br/>
        {isInvalidTicket &&
          <Typography className="has-error" component="h6" variant="h5" align="center">
            {createTicketError.map((error) => (
              <div>
                <ul>
                  <li>{error.name}</li>
                  <li>{error.description}</li>
                  <li>{error.desiredResolutionDate}</li>
                </ul>
              </div>
            ))}
          </Typography>
        }
        <div className="ticket-creation-form-container__form">
          <div className="inputs-section">
            <div className="ticket-creation-form-container__inputs-section inputs-section__ticket-creation-input ticket-creation-input ticket-creation-input_width200">
              <FormControl>
                <TextField
                  required
                  label="Name"
                  variant="outlined"
                  onChange={handleNameChange}
                  id="name-label"
                  value={name}
                />
              </FormControl>
            </div>
            <div className="inputs-section__ticket-creation-input ticket-creation-input ticket-creation-input_width200">
              <FormControl variant="outlined" required>
                <InputLabel shrink htmlFor="category-label">
                  Category
                </InputLabel>
                <Select
                  value={category}
                  label="Category"
                  onChange={handleCategoryChange}
                  inputProps={{
                    name: "category",
                    id: "category-label",
                  }}
                >
                  {CATEGORIES_OPTIONS.map((item, index) => {
                    return (
                      <MenuItem value={item.value} key={index}>
                        {item.label}
                      </MenuItem>
                    );
                  })}
                </Select>
              </FormControl>
            </div>
            <div className="inputs-section__ticket-creation-input ticket-creation-input">
              <FormControl variant="outlined" required>
                <InputLabel shrink htmlFor="urgency-label">
                  Urgency
                </InputLabel>
                <Select
                  value={urgency}
                  label="Urgency"
                  onChange={handleUrgencyChange}
                  className={"ticket-creation-input_width200"}
                  inputProps={{
                    name: "urgency",
                    id: "urgency-label",
                  }}
                >
                  {URGENCY_OPTIONS.map((item, index) => {
                    return (
                      <MenuItem value={item.value} key={index}>
                        {item.label}
                      </MenuItem>
                    );
                  })}
                </Select>
              </FormControl>
            </div>
          </div>
          <div className="inputs-section-attachment">
            <div className="inputs-section__ticket-creation-input ticket-creation-input ticket-creation-input_width200">
              <FormControl>
                <InputLabel shrink htmlFor="urgency-label">
                  Desired resolution date
                </InputLabel>
                <TextField
                  onChange={handleDesiredResolutionDate}
                  label="Desired resolution date"
                  type="date"
                  id="resolution-date"
                  value={desiredResolutionDate}
                  InputLabelProps={{
                    shrink: true,
                  }}
                />
              </FormControl>
            </div>
            <div className="ticket-creation-input">
              <FormControl>
                <Typography variant="caption">Select attachment</Typography>
                <input type="file" onChange={handleAttachmentChange} /><br/>
                <button
                  onClick={(e) => addFile(e)}
                >
                  Add File
                </button>
              </FormControl>
            </div>
          </div>
          <div>
            {addFileInfo &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                {addFileInfo}
              </Typography>
            }
            {isInvalidAttachment &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                {attachmentUploadError}
              </Typography>
            }
            {!isFileSelected &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                You should select the file first
              </Typography>
            }
            {!isFilePresent &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                {fileNotFoundError}
              </Typography>
            }
          </div>
          <div className="inputs-section">
            <FormControl>
              <TextField
                label="Description"
                multiline
                rows={4}
                variant="outlined"
                value={description}
                className="creation-text-field creation-text-field_width680"
                onChange={handleDescriptionChange}
              />
            </FormControl>
          </div>
          <div>
            {addCommentInfo &&
              <Typography className="has-error" component="h6" variant="h5" align='center'>
                {addCommentInfo}
              </Typography>
            }
            {isInvalidComment &&
              <Typography className="has-error" component="h6" variant="h5">
                {createCommentError.map((error) => (
                  <div align='center'>
                    {error.text}
                  </div>
                ))}
              </Typography>
            }
          </div>
          <div className="inputs-section">
            <FormControl>
              <TextField
                label="Comment"
                multiline
                rows={4}
                variant="outlined"
                value={comment}
                className="creation-text-field creation-text-field_width680"
                onChange={handleCommentChange}
              />
            </FormControl>
          </div>
          <section className="submit-button-section">
            <Button
              variant="contained"
              onClick={(e) => addComment(e)}
            >
              Add Comment
            </Button>
            <Button
              variant="contained"
              onClick={handleSaveDraft}
              color="secondary"
            >
              Save as Draft
            </Button>
            <Button
              variant="contained"
              onClick={handleSubmitTicket}
              color="primary"
            >
              Submit
            </Button>
          </section>
        </div>
      </div>
    );
  }
}

const TicketCreationPageWithRouter = withRouter(TicketCreationPage);
export default TicketCreationPageWithRouter;
