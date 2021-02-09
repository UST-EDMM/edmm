import '../assets/App.css';
import 'fontsource-roboto';
import Button from '@material-ui/core/Button';
import React from 'react';
import BasicTable from "../components/Table";
import TableContainer from "@material-ui/core/TableContainer";
import Paper from "@material-ui/core/Paper";
import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from '@material-ui/core/Typography';

import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import CardContent from "@material-ui/core/CardContent";
import TextareaAutosize from "@material-ui/core/TextareaAutosize";
import yaml from "js-yaml"

const rows = [
    /*
    createData('Participant A', 12345, "UPLOADED"),
    createData('Participant B', 12345, "NOT UPLOADED"),
    createData('Participant C', 12345, "NOT UPLOADED")*/
];

function createData(deploymentName, edmmID, uploadStatus, startedStatus, endpoint) {
    return { deploymentName, edmmID, uploadStatus, startedStatus, endpoint };
}

class Dashboard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            rows: rows,
            open: false,
            edmmMultiID: "",
            edmmParticipant: "",
            edmmField: "",
            openSnackbar: false
        }
        this.addParticipant = this.addParticipant.bind(this);
        this.handleClickOpen = this.handleClickOpen.bind(this);
        this.handleClose = this.handleClose.bind(this);
        this.handleChangeEDMMTextField = this.handleChangeEDMMTextField.bind(this);
    }

     addParticipant() {
         yaml.loadAll(this.state.edmmField, function(doc) {
             console.log(doc)
             rows.push(createData(doc.owner, doc.multi_id, "NOT UPLOADED", "NOT STARTED",
                 doc.participants[doc.owner].endpoint))
         })

         let base64String = Buffer.from(this.state.edmmField).toString("base64")
         console.log(base64String)

         this.setState({
            rows: rows,
            open: false
        })
    }

    handleClickOpen() {
        this.setState({
            open: true
        })
    }

    handleClose() {
        this.setState({
            open: false
        })
    }

    handleChangeEDMMTextField(event) {
        this.setState({edmmField: event.target.value});
    }

    render() {

        let tableBar;
        console.log(rows.length)
        if (rows.length !== 0) {
            tableBar = <CardContent style={{maxWidth: 1300, margin: "0 auto"
            }}>
                <TableContainer component={Paper}>
                    <Table style={{minWidth: 650}} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell><b>Deployment Name</b></TableCell>
                                <TableCell align="right"><b>EDMM ID</b></TableCell>
                                <TableCell align="right"><b>Upload Status</b></TableCell>
                                <TableCell align="right"><b>Deployment Status</b></TableCell>
                                <TableCell align="right"/>
                                <TableCell align="right"/>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            <BasicTable tableValues = {rows}/>
                        </TableBody>
                    </Table>
                </TableContainer>
            </CardContent>
        }

        return (
            <div className="App">
                <AppBar position="static" style={{background: '#263238'}}>
                    <Toolbar>
                        <Typography style={{margin: "0 auto"}} variant="h6" >
                            EDMM Deployment Orchestrator
                        </Typography>
                    </Toolbar>
                </AppBar>
                <br/>
                {tableBar}
                <div>
                    <Button variant="contained" style={{background: "#0277BD", color: 'white'}}
                            onClick={this.handleClickOpen}>
                        Add EDMM Model
                    </Button>
                    <Dialog maxWidth={"md"} fullWidth={true} open={this.state.open} onClose={this.handleClose} aria-labelledby="form-dialog-title">
                        <DialogTitle id="form-dialog-title">Upload EDMM Model to the EDMM Transformation Framework</DialogTitle>
                        <DialogContent>
                            <TextareaAutosize onChange={this.handleChangeEDMMTextField} value={this.state.edmmField} style={{width: "100%", minHeight: 30}} aria-label="minimum height" rowsMin={8} placeholder="Place YAML EDMM Model here" />
                        </DialogContent>
                        <DialogActions>
                            <Button onClick={this.handleClose} color="primary">
                                Cancel
                            </Button>
                            <Button onClick={this.addParticipant} color="primary">
                                Add Model
                            </Button>
                        </DialogActions>
                    </Dialog>
                </div>
            </div>
        );
    }
}

export default Dashboard
