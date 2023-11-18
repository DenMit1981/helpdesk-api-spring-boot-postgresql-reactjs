import React from "react";
import TabPanel from "./TabPanel";
import TicketsTable from "./TicketsTable";
import { AppBar, Button, Tab, Tabs, Typography } from "@material-ui/core";
import { Switch, Route } from "react-router-dom";
import { withRouter } from "react-router";
import TicketInfoWithRouter from "./TicketInfo";
import TicketService from '../services/TicketService';

function a11yProps(index) {
  return {
    id: `full-width-tab-${index}`,
    "aria-controls": `full-width-tabpanel-${index}`,
  };
}

class MainPage extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      prop: 42,
      tabValue: 0,
      myTickets: [],
      allTickets: [],
      filteredTickets: [],
      searchValue: '',
      searchError: [],
      pageSize: 10,
      pageNumber: 1,
      currentPage: 1,
      accessDeniedError: '',
      myTotal: 0,
      allTotal: 0
    };

    this.addTicket = this.addTicket.bind(this);
    this.handleLogout = this.handleLogout.bind(this);
  }

  componentDidMount() {
    const { pageNumber, pageSize } = this.state;

    TicketService.getAllTicketsByPages(pageSize, pageNumber).then((res) => {
      this.setState({ allTickets: res.data });
    });

    TicketService.getMyTicketsByPages(pageSize, pageNumber).then((res) => {
      this.setState({ myTickets: res.data });
    });

    TicketService.getAllTickets().then((res) => {
      this.setState({ allTotal: res.data.length });
    });

    TicketService.getMyTickets().then((res) => {
      this.setState({ myTotal: res.data.length });
    });
  }

  handlePreviousPageNumberChange = () => {
    const { pageSize, pageNumber, myTotal, allTotal, tabValue } = this.state;
    const maxAmountOfRowsOn3Pages = 3 * pageSize;

    if (tabValue === 0) {
      const numberOfPageChanges = parseInt(myTotal / maxAmountOfRowsOn3Pages);

      if(pageNumber !== 0) {
        TicketService.getMyTicketsByPages(pageSize, pageNumber - 1).then((res) => {
          this.setState({ myTickets: res.data });
          this.setState({ pageNumber: pageNumber - 1 });

          if(this.state.pageNumber - 1 > 0) {
            this.setState({ currentPage: pageNumber - numberOfPageChanges - 1});
          } else {
            this.setState({ currentPage: 1 });
          }
        });
      }
    }

    if (tabValue === 1) {
      const numberOfPageChanges = parseInt(allTotal / maxAmountOfRowsOn3Pages);

      if(pageNumber !== 0) {
        TicketService.getAllTicketsByPages(pageSize, pageNumber - 1).then((res) => {
          this.setState({ allTickets: res.data });
          this.setState({ pageNumber: pageNumber - 1 });

          if(pageNumber - 1 > 0) {
            this.setState({ currentPage: pageNumber - numberOfPageChanges - 1});
          } else {
            this.setState({ currentPage: 1 });
          }
        });
      }
    }
  }

  handlePageNumberChange = (pageNumber) => {
    const { pageSize, tabValue } = this.state;

    if (tabValue === 0) {
      TicketService.getMyTicketsByPages(pageSize, pageNumber).then((res) => {
        this.setState({ myTickets: res.data });
          this.setState({ pageNumber: pageNumber });
      });
     }

    if (tabValue === 1) {
      TicketService.getAllTicketsByPages(pageSize, pageNumber).then((res) => {
        this.setState({ allTickets: res.data });
        this.setState({ pageNumber: pageNumber });
      });
    }
  }

  handleNextPageNumberChange = () => {
    const { myTotal, allTotal, pageSize, pageNumber, tabValue } = this.state;
    const maxAmountOfRowsOn3Pages = 3 * pageSize;

    if (tabValue === 0) {
      const numberOfPageChanges = parseInt(myTotal / maxAmountOfRowsOn3Pages);
      const pagesCount = parseInt(myTotal / pageSize) ;

      if(pageNumber <= pagesCount) {
        TicketService.getMyTicketsByPages(pageSize, +pageNumber + 1).then((res) => {
          this.setState({ myTickets: res.data });
          this.setState({ pageNumber: +pageNumber + 1 });
          this.setState({ currentPage: pageNumber + numberOfPageChanges });
        });
      };
    }

    if (tabValue === 1) {
      const numberOfPageChanges = parseInt(allTotal / maxAmountOfRowsOn3Pages);
      const pagesCount = parseInt(allTotal / pageSize) ;

      if(pageNumber <= pagesCount) {
        TicketService.getAllTicketsByPages(pageSize, +pageNumber + 1).then((res) => {
          this.setState({ allTickets: res.data });
          this.setState({ pageNumber: +pageNumber + 1 });
          this.setState({ currentPage: pageNumber + numberOfPageChanges });
        });
      };
    }
  }

  addTicket() {
    const userRole = sessionStorage.getItem("userRole");

    if (userRole !== "ROLE_ENGINEER") {
      this.props.history.push('/add-ticket');
    } else {
      this.setState({ accessDeniedError: "You can't have access to create ticket" });
    }
  }

  handleLogout = () => {
    window.location.href = "/";
  };

  handleTabChange = (event, value) => {
    this.setState({
      tabValue: value,
      filteredTickets: []
    });
  };

  handleSortTicketAsc = (event, field) => {
    const { pageSize, pageNumber, searchValue, tabValue } = this.state;

    if (tabValue === 0) {
      if (searchValue === '') {
        TicketService.getMySortedTicketsByPages(field, pageSize, pageNumber).then((res) => {
          this.setState({ myTickets: res.data });
          this.setState({ filteredTickets : [] });
        });
      }

      TicketService.getMySortedTicketsByPagesSearchedByParameter(searchValue, field, pageSize, pageNumber).then((res) => {
        this.setState({ filteredTickets: res.data });
      });
    }

    if (tabValue === 1) {
      if (searchValue === '') {
        TicketService.getAllSortedTicketsByPages(field, pageSize, pageNumber).then((res) => {
          this.setState({ allTickets: res.data });
          this.setState({ filteredTickets : [] });
        });
      }

      TicketService.getAllSortedTicketsByPagesSearchedByParameter(searchValue, field, pageSize, pageNumber).then((res) => {
        this.setState({ filteredTickets: res.data });
      });
    }
  }

  handleSortTicketDesc = (event, field) => {
    const { pageSize, pageNumber, searchValue, tabValue } = this.state;

    if (tabValue === 0) {
      if (searchValue === '') {
        TicketService.getMyDescSortedTicketsByPages(field, pageSize, pageNumber).then((res) => {
          this.setState({ myTickets: res.data });
          this.setState({ filteredTickets : [] });
        });
      }

      TicketService.getMyDescSortedTicketsByPagesSearchedByParameter(searchValue, field, pageSize, pageNumber).then((res) => {
        this.setState({ filteredTickets: res.data });
      });
     }

    if (tabValue === 1) {
      if (searchValue === '') {
        TicketService.getAllDescSortedTicketsByPages(field, pageSize, pageNumber).then((res) => {
          this.setState({ allTickets: res.data });
          this.setState({ filteredTickets : [] });
        });
      }

      TicketService.getAllDescSortedTicketsByPagesSearchedByParameter(searchValue, field, pageSize, pageNumber).then((res) => {
        this.setState({ filteredTickets: res.data });
      });
     }
  }

  handleSearchTicket = (event) => {
    const { tabValue, pageNumber, pageSize } = this.state;
    const searchValue = event.target.value;

    this.setState({ searchValue: searchValue })

    if (tabValue === 0) {
      if (searchValue === '') {
        TicketService.getMyTicketsByPages(pageSize, pageNumber).then((res) => {
          this.setState({ myTickets: res.data });
          this.setState({ filteredTickets : [] });
        });
      }

      TicketService.getMyTicketsByPagesSearchedByParameter(searchValue, pageSize, pageNumber).then((res) => {
        this.setState({ filteredTickets: res.data });
        this.setState({ searchError: [] })
      }).catch(err => {
        if (err.response) {
          this.setState({ searchError: err.response.data })
        }
      });
    }

    if (tabValue === 1) {
      if (searchValue === '') {
        TicketService.getAllTicketsByPages(pageSize, pageNumber).then((res) => {
          this.setState({ allTickets: res.data });
          this.setState({ filteredTickets : [] });
        });
      }

      TicketService.getAllTicketsByPagesSearchedByParameter(searchValue, pageSize, pageNumber).then((res) => {
        this.setState({ filteredTickets: res.data });
        this.setState({ searchError: [] })
      }).catch(err => {
        if (err.response) {
          this.setState({ searchError: err.response.data })
        }
      });
     }
  }

  handleTicketStatus = (e) => {
    if (this.state.tabValue === 0) {
      let result = this.state.myTickets;

      result = result.map(todo => {
        if (todo.id === e.ticketId) {
          todo.status = e.newStatus;
        }

        return todo;
      })

      this.setState({ myTickets: result })
    }

    if (this.state.tabValue === 1) {
      let result = this.state.allTickets;

      result = result.map(todo => {
        if (todo.id === e.ticketId) {
          todo.status = e.newStatus;
        }

        return todo;
      })

      this.setState({ allTickets: result })
    }
  }

  render() {
    const { allTickets, myTickets, filteredTickets, tabValue, searchError,
            accessDeniedError, pageNumber, currentPage, myTotal, allTotal } = this.state;
    const { path } = this.props.match;
    const { handleSortTicketAsc, handleSortTicketDesc, handleSearchTicket, addTicket,
            handleTicketStatus, handlePageNumberChange, handlePreviousPageNumberChange,
            handleNextPageNumberChange, handleLogout, handleTabChange } = this;

    return (
        <Switch>
          <Route exact path={path}>
            <div className="buttons-container">
              <Button
                onClick={addTicket}
                variant="contained"
                color="primary"
              >
                Create Ticket
              </Button>
              <div align="center">
                {accessDeniedError &&
                  <Typography className="has-error" component="h6" variant="h5">
                    {accessDeniedError}
                  </Typography>
                }
                {searchError &&
                  <Typography className="has-error" component="h6" variant="h5">
                    {searchError}
                  </Typography>
                }
              </div>
              <Button
                onClick={handleLogout}
                variant="contained"
                color="secondary"
              >
                Logout
              </Button>
            </div>
            <div className="table-container">
              <AppBar position="static">
                <Tabs
                  variant="fullWidth"
                  onChange={handleTabChange}
                  value={tabValue}
                >
                  <Tab label="My tickets" {...a11yProps(0)} />
                  <Tab label="All tickets" {...a11yProps(1)} />
                </Tabs>
                <TabPanel value={tabValue} index={0}>
                  <TicketsTable
                    sortAscCallback={handleSortTicketAsc}
                    sortDescCallback={handleSortTicketDesc}
                    searchCallback={handleSearchTicket}
                    ticketsStatus={handleTicketStatus}
                    tickets={
                      filteredTickets.length ? filteredTickets : myTickets
                    }
                    total = {myTotal}
                    pageNumber = {pageNumber}
                    selected = {
                      filteredTickets.length ? filteredTickets.length : 0
                    }
                  />
                </TabPanel>
                <TabPanel value={tabValue} index={1}>
                  <TicketsTable
                    sortAscCallback={handleSortTicketAsc}
                    sortDescCallback={handleSortTicketDesc}
                    searchCallback={handleSearchTicket}
                    ticketsStatus={handleTicketStatus}
                    tickets={
                      filteredTickets.length ? filteredTickets : allTickets
                    }
                    total = {allTotal}
                    pageNumber = {pageNumber}
                    selected = {
                      filteredTickets.length ? filteredTickets.length : 0
                    }
                  />
                </TabPanel>
              </AppBar>
            </div>
            <div>
              <table>
                <tr className="table">
                  <td>
                    <div  className="container__button-wrapper">
                      <button
                        size="large"
                        variant="contained"
                        color="primary"
                        type="reset"
                        onClick={handlePreviousPageNumberChange}
                      >
                        Previous
                      </button>
                    </div>
                  </td>
                  <td>
                    <div className="container__button-wrapper">
                      <button
                        size="large"
                        variant="contained"
                        color="primary"
                        type="reset"
                        onClick={() => handlePageNumberChange(currentPage)}
                      >
                        {currentPage}
                      </button>
                    </div>
                  </td>
                  <td>
                    <div className="container__button-wrapper">
                      <button
                        size="large"
                        variant="contained"
                        color="secondary"
                        type="reset"
                        onClick={() => handlePageNumberChange(currentPage + 1)}
                      >
                        {currentPage + 1}
                      </button>
                    </div>
                  </td>
                  <td>
                    <div className="container__button-wrapper">
                      <button
                        size="large"
                        variant="contained"
                        color="primary"
                        type="reset"
                        onClick={() => handlePageNumberChange(currentPage + 2)}
                      >
                        {currentPage + 2}
                      </button>
                    </div>
                  </td>
                  <td>
                    <div className="container__button-wrapper">
                      <button
                        size="large"
                        variant="contained"
                        color="primary"
                        type="reset"
                        onClick={handleNextPageNumberChange}
                      >
                        Next
                      </button>
                    </div>
                  </td>
                </tr>
              </table>
            </div>
          </Route>
          <Route path={`${path}/:ticketId`}>
            <TicketInfoWithRouter />
          </Route>
        </Switch>
    );
  }
}

const MainPageWithRouter = withRouter(MainPage);
export default MainPageWithRouter;
