import React from "react";
import PropTypes from "prop-types";
import {
  ButtonGroup,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography
} from "@material-ui/core";
import { Link } from "react-router-dom";
import { withRouter } from "react-router";
import { TICKETS_TABLE_COLUMNS } from "../constants/tablesColumns";
import TicketService from '../services/TicketService';

class TicketsTable extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      id: this.props.match.params.id,
      page: 0,
      rowsPerPage: 100,
      changeStatusError: [],
    };
  }

  handleCancelTicket = (id, previousStatus) => {
    TicketService.changeTicketStatusToCanceled(id).then((res) => {
      if (res.status === 201) {
        this.props.ticketsStatus({ newStatus: 'CANCELED', ticketId: id });
      }
    }).catch(err => {
      if (err.response) {
        this.setState({ changeStatusError: err.response.data.info })
      }
    });
  }

  handleSubmitTicket = (id, previousStatus) => {
    TicketService.changeTicketStatusToNew(id).then((res) => {
      if (res.status === 201) {
        this.props.ticketsStatus({ newStatus: 'NEW', ticketId: id });
      }
    }).catch(err => {
      if (err.response) {
        this.setState({ changeStatusError: err.response.data.info })
      }
    });
  }

  handleApproveTicket = (id, previousStatus) => {
    TicketService.changeTicketStatusToApproved(id).then((res) => {
      if (res.status === 201) {
        this.props.ticketsStatus({ newStatus: 'APPROVED', ticketId: id });
      }
    }).catch(err => {
      if (err.response) {
        this.setState({ changeStatusError: err.response.data.info })
      }
    });
  }

  handleDeclineTicket = (id, previousStatus) => {
    TicketService.changeTicketStatusToDeclined(id).then((res) => {
      if (res.status === 201) {
        this.props.ticketsStatus({ newStatus: 'DECLINED', ticketId: id });
      }
    }).catch(err => {
      if (err.response) {
        this.setState({ changeStatusError: err.response.data.info })
      }
    });
  }

  handleAssignTicket = (id, previousStatus) => {
    TicketService.changeTicketStatusToInProgress(id).then((res) => {
      if (res.status === 201) {
        this.props.ticketsStatus({ newStatus: 'IN_PROGRESS', ticketId: id });
      }
    }).catch(err => {
      if (err.response) {
        this.setState({ changeStatusError: err.response.data.info })
      }
    });
  }

  handleDoneTicket = (id, previousStatus) => {
    TicketService.changeTicketStatusToDone(id).then((res) => {
      if (res.status === 201) {
        this.props.ticketsStatus({ newStatus: 'DONE', ticketId: id });
      }
    }).catch(err => {
      if (err.response) {
        this.setState({ changeStatusError: err.response.data.info })
      }
    });
  }

  render() {
    const { tickets, sortAscCallback, sortDescCallback, searchCallback, total, selected, pageNumber } = this.props;
    const { page, rowsPerPage, changeStatusError } = this.state;
    const { url } = this.props.match;
    const {
      handleCancelTicket,
      handleSubmitTicket,
      handleApproveTicket,
      handleDeclineTicket,
      handleAssignTicket,
      handleDoneTicket
    } = this;
    const userRole = sessionStorage.getItem("userRole");

    return (
      <Paper>
        {changeStatusError &&
          <div align="center">
            <Typography className="has-error" component="h6" variant="h5">
              {changeStatusError}
            </Typography>
          </div>
        }
        <TableContainer>
          <TextField
            onChange={searchCallback}
            id="filled-full-width"
            label="Search"
            style={{ margin: 5, width: "200px" }}
            placeholder="Enter text to search"
            margin="normal"
            InputLabelProps={{
              shrink: true,
            }}
          />
          <Table>
            <TableHead>
              <TableRow>
                {TICKETS_TABLE_COLUMNS.map((column) => (
                  <TableCell align={column.align} key={column.id}>
                    <div key={column.id}>
                      <div class="field"><b>{column.label}</b></div>
                      {column.label !== 'Action' &&
                        <div class="up-arrow" onClick={(e) => sortAscCallback(e, column.id)}></div>
                      }
                      {column.label !== 'Action' &&
                        <div class="down-arrow" onClick={(e) => sortDescCallback(e, column.id)}></div>
                      }
                    </div>
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {tickets
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((row, index) => {
                  return (
                    <TableRow hover role="checkbox" key={index}>
                      {TICKETS_TABLE_COLUMNS.map((column) => {
                        const value = row[column.id];
                        if (column.id === "name") {
                          return (
                            <TableCell key={column.id}>
                              <Link to={`${url}/${row.id}`}>{value}</Link>
                            </TableCell>
                          );
                        }
                        if (column.id === "action") {
                          if (row.status === "DRAFT" && (userRole === "ROLE_MANAGER" || "ROLE_EMPLOYEE")) {
                            return <TableCell align="center" key={column.id}>
                              <ButtonGroup>
                                <Button
                                  onClick={() => handleCancelTicket(row.id, row.status)}
                                  variant="contained"
                                  color="secondary"
                                >
                                  Cancel
                                </Button>
                                <Button
                                  onClick={() => handleSubmitTicket(row.id, row.status)}
                                  variant="contained"
                                  color="primary"
                                >
                                  Submit
                                </Button>
                              </ButtonGroup>
                            </TableCell>
                          } else if (row.status === "NEW" && userRole === "ROLE_MANAGER") {
                            return <TableCell align="center" key={column.id}>
                              <ButtonGroup>
                                <Button
                                  onClick={() => handleApproveTicket(row.id, row.status)}
                                  variant="contained"
                                  color="secondary"
                                >
                                  Approve
                                </Button>
                                <Button
                                  onClick={() => handleDeclineTicket(row.id, row.status)}
                                  variant="contained"
                                  color="primary"
                                >
                                  Decline
                                </Button>
                                <Button
                                  onClick={() => handleCancelTicket(row.id, row.status)}
                                  variant="contained"
                                  color="primary"
                                >
                                  Cancel
                                </Button>
                              </ButtonGroup>
                            </TableCell>
                          } else if (row.status === "APPROVED" && userRole === "ROLE_ENGINEER") {
                            return <TableCell align="center" key={column.id}>
                              <ButtonGroup>
                                <Button
                                  onClick={() => handleAssignTicket(row.id, row.status)}
                                  variant="contained"
                                  color="secondary"
                                >
                                  Assign to me
                                </Button>
                                <Button
                                  onClick={() => handleCancelTicket(row.id, row.status)}
                                  variant="contained"
                                  color="primary"
                                >
                                  Cancel
                                </Button>
                              </ButtonGroup>
                            </TableCell>
                          } else if (row.status === "DECLINED" && (userRole === "ROLE_MANAGER" || userRole === "ROLE_EMPLOYEE")) {
                            return <TableCell align="center" key={column.id}>
                              <ButtonGroup>
                                <Button
                                  onClick={() => handleCancelTicket(row.id, row.status)}
                                  variant="contained"
                                  color="secondary"
                                >
                                  Cancel
                                </Button>
                                <Button
                                  onClick={() => handleSubmitTicket(row.id, row.status)}
                                  variant="contained"
                                  color="primary"
                                >
                                  Submit
                                </Button>
                              </ButtonGroup>
                            </TableCell>
                          } else if (row.status === "IN_PROGRESS" && userRole === "ROLE_ENGINEER") {
                            return <TableCell align="center" key={column.id}>
                              <ButtonGroup>
                                <Button
                                  onClick={() => handleDoneTicket(row.id, row.status)}
                                  variant="contained"
                                  color="secondary"
                                >
                                  Done
                                </Button>
                              </ButtonGroup>
                            </TableCell>
                          } else if (row.status === "DONE" && (userRole === "ROLE_MANAGER" || userRole === "ROLE_EMPLOYEE")) {
                            return <TableCell align="center" key={column.id}>
                              <ButtonGroup>
                                <Button
                                  component={Link}
                                  to={`/feedbacks/${row.id}`}
                                  variant="contained"
                                  color="primary"
                                >
                                  CREATE FEEDBACK
                                </Button>
                              </ButtonGroup>
                            </TableCell>
                          } else {
                            return <TableCell key={column.id}></TableCell>
                          }
                        } else {
                          return <TableCell key={column.id}>{value}</TableCell>;
                        }
                      })}
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table><br/>
          <Typography align="right" component="h5" variant="h5">
            Page: {pageNumber}
          </Typography>
          {selected !== 0 &&
            <Typography align="right" component="h5" variant="h5">
              Selected: {selected}
            </Typography>
          }
          <Typography align="right" component="h5" variant="h5">
            Total: {total}
          </Typography>
        </TableContainer>
      </Paper>
    );
  }
}

TicketsTable.propTypes = {
  tickets: PropTypes.array,
  total: PropTypes.number,
  selected: PropTypes.number,
  pageNumber: PropTypes.number
};

const TicketsTableWithRouter = withRouter(TicketsTable);
export default TicketsTableWithRouter;
