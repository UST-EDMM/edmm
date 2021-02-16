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

import CardContent from "@material-ui/core/CardContent";
import yaml from "js-yaml"
import {postUploadModel} from "../api/TransformationFrameworkAPI";

const rows = [
    /*
    createData('Participant A', 12345, "UPLOADED"),
    createData('Participant B', 12345, "NOT UPLOADED"),
    createData('Participant C', 12345, "NOT UPLOADED")*/
];

function createData(deploymentName, edmmID, uploadStatus, startedStatus, endpoint, content) {
    return { deploymentName, edmmID, uploadStatus, startedStatus, endpoint, content };
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
    }

     addParticipant(event) {
         const reader = new FileReader()

         reader.onload = async (e) => {
             yaml.loadAll(e.target.result, function(doc) {
                 console.log(doc)
                 rows.push(createData(doc.owner, doc.multi_id, "NOT UPLOADED", "NOT STARTED",
                     doc.participants[doc.owner].endpoint, e.target.result))

                 let base64String = Buffer.from(e.target.result).toString("base64")
                 console.log(base64String)

                 postUploadModel(doc.participants[doc.owner].endpoint, base64String, doc.multi_id)
             })


             this.setState({
                 rows: rows,
                 open: false
             })
         }
         reader.readAsText(event.target.files[0])

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
                    <input
                        style={{display: "none"}}
                        accept="*/*"
                        id="contained-button-file"
                        multiple
                        type="file"
                        onChange={e => this.addParticipant(e)}
                    />
                    <label htmlFor="contained-button-file">
                        <Button style={{background: "#0277BD", color: 'white'}} variant="contained" color="primary" component="span">
                            Add EDMM Model
                        </Button>
                    </label>
                </div>
            </div>
        );
    }
}

export default Dashboard
