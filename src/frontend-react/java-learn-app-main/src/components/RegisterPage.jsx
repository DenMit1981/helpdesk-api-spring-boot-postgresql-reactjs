import React from "react";
import { Button, TextField, Typography } from "@material-ui/core";
import AuthenticationService from '../services/AuthenticationService';

class RegisterPage extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      registerValidErrors: [],
      isUserPresent: false,
      userIsPresentErrors: '',
    };
    this.signIn = this.signIn.bind(this);
  }

  handleFirstNameChange = (event) => {
    this.setState({ firstName: event.target.value });
  };

  handleLastNameChange = (event) => {
    this.setState({ lastName: event.target.value });
  };

  handleEmailChange = (event) => {
    this.setState({ email: event.target.value });
  };

  handlePasswordChange = (event) => {
    this.setState({ password: event.target.value });
  };

  signIn() {
    const { firstName, lastName, email, password } = this.state;

    AuthenticationService
      .createUser(firstName, lastName, email, password)
      .then((response) => {
        this.props.history.push('/')
      })
      .catch(err => {
         if (err.response.status === 401) {
            this.setState({ registerValidErrors: err.response.data });
            this.setState({ userIsPresentErrors: '' });
         }
         if (err.response.status === 400) {
            this.setState({ isUserPresent: true })
            this.setState({ userIsPresentErrors: err.response.data.info });
            this.setState({ registerValidErrors: [] });
         }
      })
  }

  render() {
    const { isUserPresent, userIsPresentErrors, registerValidErrors } = this.state;
    const { handleFirstNameChange, handleLastNameChange, handleEmailChange, handlePasswordChange, signIn } = this;

    return (
      <div className="container">
        <div className="container__title-wrapper">
          <Typography component="h2" variant="h3">
            Register page
          </Typography>
        </div>
        <div className="has-error">
        {isUserPresent &&
             <div>
                {userIsPresentErrors}
             </div>
        }
        {registerValidErrors &&
            <div className="has-error">
              <ol>
                {registerValidErrors.map((key) => {
                  return <li>{key} </li>
                })}
              </ol>
            </div>
        }
        </div>
        <div className="container__from-wrapper">
          <form>
              <div className="form__input-wrapper">
                <div className="container__title-wrapper">
                   <Typography component="h6" variant="h5">
                      First name
                   </Typography>
                </div>
                <TextField
                  onChange={handleFirstNameChange}
                  label="Name"
                  variant="outlined"
                  placeholder="Enter your first name"
                />
              </div>
              <div className="form__input-wrapper">
                <div className="container__title-wrapper">
                   <Typography component="h6" variant="h5">
                      Last name
                   </Typography>
                </div>
                <TextField
                  onChange={handleLastNameChange}
                  label="Last name"
                  variant="outlined"
                  placeholder="Enter your last name"
                />
              </div>
              <div className="form__input-wrapper">
                <div className="container__title-wrapper">
                   <Typography component="h6" variant="h5">
                      Email
                   </Typography>
                </div>
                <TextField
                  onChange={handleEmailChange}
                  label="Email"
                  variant="outlined"
                  placeholder="Enter your email"
                />
              </div>
              <div className="form__input-wrapper">
                <div className="container__title-wrapper">
                   <Typography component="h6" variant="h5">
                      Password
                   </Typography>
                </div>
                <TextField
                  onChange={handlePasswordChange}
                  label="Password"
                  variant="outlined"
                  type="password"
                  placeholder="Enter your password"
                />
              </div>
          </form>
        </div>
        <div className="container__button-wrapper">
          <Button
            size="large"
            variant="contained"
            color="primary"
            onClick={signIn}
          >
            Sign In
          </Button>
        </div>
      </div>
    );
  }
};

export default RegisterPage;